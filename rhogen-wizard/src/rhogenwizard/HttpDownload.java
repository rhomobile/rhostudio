package rhogenwizard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownload
{
    public static class HttpException extends IOException
    {
        private static final long serialVersionUID = -1018042761849715403L;

        public HttpException(IOException e)
        {
            super(e);
        }
    }

    public static class StreamException extends IOException
    {
        private static final long serialVersionUID = -3224144258724889642L;

        public StreamException(IOException e)
        {
            super(e);
        }
    }

    private class Downloader implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    InputStream stream = getInputStream();
                    if (stream == null)
                    {
                        return;
                    }
                    try
                    {
                        copy(stream, m_stream);
                    }
                    finally
                    {
                        close(stream);
                    }
                    break;
                }
                catch (HttpException e)
                {
                    m_exception = e;
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e1)
                    {
                        break;
                    }
                }
                catch (StreamException e)
                {
                    m_exception = e;
                    break;
                }
            }
        }

        private InputStream getInputStream() throws HttpException
        {
            try
            {
                HttpURLConnection connection = (HttpURLConnection) m_url.openConnection();
                connection.setRequestProperty("Range", "bytes=" + m_downloaded + "-");
                connection.connect();

                m_responseCode = connection.getResponseCode();
                if (!goodResponseCode())
                {
                    return null;
                }

                long length = connection.getContentLength();
                m_size = (length == -1) ? -1 : m_downloaded + length;

                return connection.getInputStream();
            }
            catch (IOException e)
            {
                throw new HttpException(e);
            }
        }

        private void close(InputStream is)
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
            }
        }

        private void copy(InputStream is, OutputStream os) throws HttpException, StreamException
        {
            byte[] buffer = new byte[16 * 1024];
            while (true)
            {
                int read;
                try
                {
                    read = is.read(buffer);
                }
                catch (IOException e)
                {
                    throw new HttpException(e);
                }

                if (read == -1)
                {
                    break;
                }

                try
                {
                    os.write(buffer, 0, read);
                }
                catch (IOException e)
                {
                    throw new StreamException(e);
                }

                m_downloaded += read;
            }
        }
    }

    private final URL            m_url;
    private final OutputStream   m_stream;
    private volatile long        m_size;
    private volatile long        m_downloaded;
    private volatile IOException m_exception;
    private volatile int         m_responseCode;
    private final Thread         m_downloader;

    public HttpDownload(URL url, OutputStream stream)
    {
        m_url = url;
        m_stream = stream;
        m_size = -1;
        m_downloaded = 0;
        m_exception = null;
        m_responseCode = 0;
        m_downloader = new Thread(new Downloader());
        m_downloader.start();
    }

    public void stop()
    {
        m_downloader.interrupt();
    }

    public boolean isAlive()
    {
        return m_downloader.isAlive();
    }

    public void join(int millis) throws InterruptedException
    {
        m_downloader.join(millis);
    }

    public boolean ok()
    {
        return goodResponseCode() && m_exception == null;
    }

    public int getResponseCode()
    {
        return m_responseCode;
    }

    public IOException getException()
    {
        return m_exception;
    }

    public long getSize()
    {
        return m_size;
    }

    public long getDownloaded()
    {
        return m_downloaded;
    }

    private boolean goodResponseCode()
    {
        return m_responseCode == HttpURLConnection.HTTP_OK
            || m_responseCode == HttpURLConnection.HTTP_PARTIAL;
    }
}
