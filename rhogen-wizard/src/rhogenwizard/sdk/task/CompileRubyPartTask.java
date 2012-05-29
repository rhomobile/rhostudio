package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.ILogDevice;

public class CompileRubyPartTask extends ARubyTask
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

    private final List<String> m_outputStrings;

    public CompileRubyPartTask(String workDir)
    {
        super(workDir, "rake", "build:bundle:rhostudio");

        m_outputStrings = new ArrayList<String>();

        m_executor.setOutputLogDevice(nullLogDevice);
        m_executor.setErrorLogDevice(new OutputAdapter());
    }

    @Override
    protected void exec()
    {
        m_outputStrings.clear();
        super.exec();
        m_taskResult.put(outStrings, m_outputStrings);
    }
}
