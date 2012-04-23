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

    private static ILogDevice nullLogDevice = new ILogDevice()
    {
        @Override
        public void log(String str)
        {
        }
    };

    public static final String outStrings = "cmd-output";

    public final List<String> m_outputStrings;
    private final OutputAdapter m_outputHolder;

    public CompileRubyPartTask()
    {
        m_outputStrings = new ArrayList<String>();
        m_outputHolder = new OutputAdapter();
        m_executor.setOutputLogDevice(nullLogDevice);
        m_executor.setErrorLogDevice(m_outputHolder);
    }

    @Override
    public void run()
    {
        try
        {
            m_outputStrings.clear();

            String workDir = (String) m_taskParams.get(IRunTask.workDir);

            m_executor.setWorkingDirectory(workDir);

            List<String> cmdLine = Arrays.asList(m_rakeExe, "build:bundle:rhostudio");

            int res = m_executor.runCommand(cmdLine);

            m_taskResult.put(resTag, res);
            m_taskResult.put(outStrings, m_outputStrings);
        }
        catch (Exception e)
        {
            m_taskResult.put(resTag, TaskResultConverter.failCode);
        }
    }
}
