package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ARakeTask extends RakeTask
{
    private final String       m_workDir;
    private final List<String> m_cmdLine = new ArrayList<String>();

    public ARakeTask(String workDir, String... tasks)
    {
        m_workDir = workDir;
        m_cmdLine.add(m_rakeExe);
        m_cmdLine.addAll(Arrays.asList(tasks));
    }

    public String getOutput()
    {
        return m_executor.getCommandOutput();
    }

    @Override
    protected void exec()
    {
        try
        {
            m_executor.setWorkingDirectory(m_workDir);
            m_executor.runCommand(m_cmdLine);
        }
        catch (Exception e)
        {
        }
    }
}
