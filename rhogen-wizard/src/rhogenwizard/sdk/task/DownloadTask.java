package rhogenwizard.sdk.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.HttpDownload;

public class DownloadTask extends RunTask
{
    private final URL    m_url;
    private final String m_fileName;
    private int          m_restToWork;
    private long         m_restToDownload;
    private boolean m_ok;

    public DownloadTask(String url, String fileName, int toWork) throws MalformedURLException
    {
        m_url = new URL(url);
        m_fileName = fileName;
        m_restToWork = toWork;
        m_restToDownload = -1;
        m_ok = false;
    }

    @Override
    public boolean isOk()
    {
        return m_ok;
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            throw new StoppedException();
        }

        OutputStream stream;
        try
        {
            stream = new FileOutputStream(m_fileName);
        }
        catch (FileNotFoundException e)
        {
            throw new StoppedException(e);
        }

        try
        {
            HttpDownload download = new HttpDownload(m_url, stream);

            while (download.isAlive())
            {
                try
                {
                    download.join(100);
                }
                catch (InterruptedException e)
                {
                    throw new StoppedException(e);
                }

                if (monitor.isCanceled())
                {
                    download.stop();
                    throw new StoppedException();
                }

                long size = download.getSize();
                if (size >= 0)
                {
                    if (m_restToDownload == -1)
                    {
                        m_restToDownload = size;
                    }

                    if (m_restToDownload > 0)
                    {
                        long restToDownload = size - download.getDownloaded();
                        long downloaded = m_restToDownload - restToDownload;
                        long round = m_restToDownload / 2;
                        int worked = (int) ((m_restToWork * downloaded + round) / m_restToDownload);

                        m_restToDownload -= downloaded;
                        m_restToWork -= worked;
                        monitor.worked(worked);
                    }
                }
            }
            monitor.worked(m_restToWork);
        }
        catch (RuntimeException e)
        {
            try
            {
                stream.close();
            }
            catch (IOException e1)
            {
            }
            new File(m_fileName).delete();
            throw e;
        }

        try
        {
            stream.close();
        }
        catch (IOException e)
        {
            throw new StoppedException(e);
        }
        
        m_ok = true;
    }
}
