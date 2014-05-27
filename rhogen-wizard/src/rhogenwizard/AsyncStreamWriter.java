package rhogenwizard;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class AsyncStreamWriter extends Thread
{
    private final OutputStream m_outputStream;
    private final String       m_text;

    public AsyncStreamWriter(OutputStream outputStream, String text)
    {
        m_outputStream = outputStream;
        m_text = text;
    }

    @Override
    public void run()
    {
        OutputStreamWriter osw = new OutputStreamWriter(m_outputStream);
        try
        {
            if (m_text != null)
            {
                osw.write(m_text);
            }
            osw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
