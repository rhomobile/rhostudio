package rhogenwizard;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SysCommandExecutor
{
    private static int waitReadingTime = 500;

    private ILogDevice m_ouputLogDevice = null;
    private ILogDevice m_errorLogDevice = null;
    private String     m_workingDirectory = null;
    private List<EnvironmentVar> m_environmentVarList = null;

    private StringBuffer      m_cmdOutput = null;
    private StringBuffer      m_cmdError = null;
    private AsyncStreamReader m_cmdOutputThread = null;
    private AsyncStreamReader m_cmdErrorThread = null;

    public void setOutputLogDevice(ILogDevice logDevice)
    {
        m_ouputLogDevice = logDevice;
    }

    public void setErrorLogDevice(ILogDevice logDevice)
    {
        m_errorLogDevice = logDevice;
    }

    public void setWorkingDirectory(String workingDirectory) {
        m_workingDirectory = workingDirectory;
    }

    public void setEnvironmentVar(String name, String value)
    {
        if( m_environmentVarList == null )
            m_environmentVarList = new ArrayList<EnvironmentVar>();

        m_environmentVarList.add(new EnvironmentVar(name, value));
    }

    public String getCommandOutput()
    {
        return m_cmdOutput.toString();
    }

    public String getCommandError()
    {
        return m_cmdError.toString();
    }

    public int runCommand(List<String> commandLine) throws IOException, InterruptedException
    {
        if (m_cmdOutput != null)
        {
            m_cmdOutput.delete(0, m_cmdOutput.length());
        }

        /* run command */
        Process process = runCommandHelper(commandLine);

        /* start output and error read threads */
        startOutputAndErrorReadThreads(process.getInputStream(), process.getErrorStream());

        /* wait for command execution to terminate */
        try
        {
            return process.waitFor();
        }
        finally
        {
            /* notify output and error read threads to stop reading */
            notifyOutputAndErrorReadThreadsToStopReading();
        }
    }

    private Process runCommandHelper(List<String> commandLine) throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder(commandLine);

        if (m_workingDirectory != null) {
            pb.directory(new File(m_workingDirectory));
        }

        return pb.start();
    }

    private void startOutputAndErrorReadThreads(InputStream processOut, InputStream processErr)
    {
        m_cmdOutput = new StringBuffer();
        m_cmdOutputThread = new AsyncStreamReader(false, processOut, m_cmdOutput, m_ouputLogDevice, "OUTPUT");
        m_cmdOutputThread.start();

        m_cmdError = new StringBuffer();
        m_cmdErrorThread = new AsyncStreamReader(false, processErr, m_cmdError, m_errorLogDevice, "ERROR");
        m_cmdErrorThread.start();
    }

    private void notifyOutputAndErrorReadThreadsToStopReading() throws InterruptedException
    {
        m_cmdOutputThread.join(1000);
        if (m_cmdOutputThread.isAlive())
        {
            m_cmdOutputThread.stopReading();
        }

        m_cmdErrorThread.join(1000);
        if (m_cmdErrorThread.isAlive())
        {
            m_cmdErrorThread.stopReading();
        }
    }

    private String[] getEnvTokens()
    {
        if( m_environmentVarList == null )
            return null;

        String[] envTokenArray = new String[m_environmentVarList.size()];
        Iterator<EnvironmentVar> envVarIter = m_environmentVarList.iterator();
        int nEnvVarIndex = 0;
        while (envVarIter.hasNext() == true)
        {
            EnvironmentVar envVar = (EnvironmentVar)(envVarIter.next());
            String envVarToken = envVar.m_envName + "=" + envVar.m_envValue;
            envTokenArray[nEnvVarIndex++] = envVarToken;
        }

        return envTokenArray;
    }
}

class EnvironmentVar
{
    public String m_envName = null;
    public String m_envValue = null;

    public EnvironmentVar(String name, String value)
    {
        m_envName = name;
        m_envValue = value;
    }
}

