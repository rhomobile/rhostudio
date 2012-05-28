package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rhogenwizard.ILogDevice;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class CompileRubyPartTask extends RakeTask
{
    private class OutputAdapter implements ILogDevice
    {
        @Override
        public void log(String str)
        {
            m_outputStrings.add(str);
        }
    }

    private static ILogDevice  nullLogDevice = new ILogDevice()
                                             {
                                                 @Override
                                                 public void log(String str)
                                                 {
                                                 }
                                             };

    public static final String outStrings    = "cmd-output";

    private final String       m_workDir;
    private final List<String> m_outputStrings;

    public CompileRubyPartTask(String workDir)
    {
        m_outputStrings = new ArrayList<String>();
        m_executor.setOutputLogDevice(nullLogDevice);
        m_executor.setErrorLogDevice(new OutputAdapter());

        m_workDir = workDir;
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = Arrays.asList(m_rakeExe, "build:bundle:rhostudio");

        m_outputStrings.clear();
        m_taskResult.clear();
        int result = TaskResultConverter.failCode;

        try
        {
            m_executor.setWorkingDirectory(m_workDir);
            result = m_executor.runCommand(cmdLine);
        }
        catch (Exception e)
        {
        }

        m_taskResult.put(resTag, result);
        m_taskResult.put(outStrings, m_outputStrings);
    }
}
