package rhogenwizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncStreamReader extends Thread
{
	private static int maxWaitingTimeForReadFile = 500;
	private static int waitingTimePortion = 50;
	
	private StringBuffer m_buffer = null;
	private InputStream  m_inputStream = null;
	private String       m_threadId = null;
	private boolean      m_stopFlag = false;
	private boolean      m_readFile = false;
	private ILogDevice   m_logDevice = null;
	
	private String fNewLine = null;
	
	public AsyncStreamReader(boolean readFile, InputStream inputStream, StringBuffer buffer, ILogDevice logDevice, String threadId)
	{
		m_readFile    = readFile;
		m_inputStream = inputStream;
		m_buffer      = buffer;
		m_threadId    = threadId;
		m_logDevice   = logDevice;
		
		fNewLine = System.getProperty("line.separator");
	}	
	
	public String getBuffer() 
	{		
		return m_buffer.toString();
	}
	
	@Override
    public void run()
	{
		try 
		{
			if (m_readFile)
			{
				readFile();
			}
			else
			{
				readCommandOutput();
			}
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace(); //DEBUG
		}
	}
	
	private void readFile() throws IOException, InterruptedException 
	{
		BufferedReader bufOut = new BufferedReader(new InputStreamReader(m_inputStream));
		String line = null;
		
		try
		{
			while (m_stopFlag == false)
			{
				if (bufOut.ready())
				{
					line = bufOut.readLine();
					m_buffer.append(line + fNewLine);
					printToDisplayDevice(line);				
				}
				else
				{
					int timeCounter = 0;
					
					while(timeCounter < maxWaitingTimeForReadFile)
					{
						Thread.sleep(waitingTimePortion);
						timeCounter += waitingTimePortion;
						
						if (m_stopFlag)
							break;
					}
				}
			}
		}
		finally
		{
			bufOut.close();
			m_inputStream.close(); //TODO -- need test it
		}
	}
	
	private void readCommandOutput() throws Exception
	{		
		BufferedReader bufOut = new BufferedReader(new InputStreamReader(m_inputStream));		
		String line = null;
		
		while ( (m_stopFlag == false) && ((line = bufOut.readLine()) != null) )
		{
			m_buffer.append( line + fNewLine);
			printToDisplayDevice(line);
			line = null;
		}		

		bufOut.close();
	}
	
	public void stopReading() 
	{
		printToConsole("call stoping " + m_threadId); //DEBUG
		m_stopFlag = true;
	}
	
	private void printToDisplayDevice(String line)
	{
		if( m_logDevice != null )
			m_logDevice.log(line);
		else
		{
			printToConsole(line);//DEBUG
		}
	}
	
	private synchronized void printToConsole(String line) 
	{
		System.out.println(line);
	}
}