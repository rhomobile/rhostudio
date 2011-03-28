package rhogenwizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncStreamReader extends Thread
{
	private StringBuffer fBuffer = null;
	private InputStream fInputStream = null;
	private String fThreadId = null;
	private boolean fStop = false;
	private ILogDevice fLogDevice = null;
	
	private String fNewLine = null;
	
	public AsyncStreamReader(InputStream inputStream, StringBuffer buffer, ILogDevice logDevice, String threadId)
	{
		fInputStream = inputStream;
		fBuffer = buffer;
		fThreadId = threadId;
		fLogDevice = logDevice;
		
		fNewLine = System.getProperty("line.separator");
	}	
	
	public String getBuffer() {		
		return fBuffer.toString();
	}
	
	public void run()
	{
		try 
		{
			readCommandOutput();
			Thread.sleep(100);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace(); //DEBUG
		}
	}
	
	private void readCommandOutput() throws IOException, InterruptedException
	{		
		BufferedReader bufOut = new BufferedReader(new InputStreamReader(fInputStream));		
		String line = null;
		while ( (fStop == false) )
		{
			if (fInputStream.available() > 0)
			{
				line = bufOut.readLine();
				
				fBuffer.append(line + fNewLine);
				printToDisplayDevice(line);				
			}
			else
			{
				Thread.sleep(500);
			}
		}		
		bufOut.close();
		printToConsole("END OF: " + fThreadId); //DEBUG
	}
	
	public void stopReading() {
		fStop = true;
	}
	
	private void printToDisplayDevice(String line)
	{
		if( fLogDevice != null )
			fLogDevice.log(line);
		else
		{
			printToConsole(line);//DEBUG
		}
	}
	
	private synchronized void printToConsole(String line) {
		System.out.println(line);
	}
}