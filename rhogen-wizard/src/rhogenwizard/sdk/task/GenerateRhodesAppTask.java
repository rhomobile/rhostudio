package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesAppTask extends RhodesTask
{
    public static final String appName = "appname";

    @Override
    public void run()
    {
        m_taskResult.clear();
        int result = TaskResultConverter.failCode;
        try
        {
            if (m_taskParams == null || m_taskParams.size() == 0)
                throw new IllegalArgumentException(
                        "parameters data is invalid [GenerateRhodesAppTask]");

            String workDir = (String) m_taskParams.get(GenerateRhodesAppTask.workDir);
            String appName = (String) m_taskParams.get(GenerateRhodesAppTask.appName);

            m_executor.setWorkingDirectory(workDir);

            List<String> cmdLine = new ArrayList<String>();
            cmdLine.add(m_rhogenExe);
            cmdLine.add("app");
            cmdLine.add(appName);

            result = m_executor.runCommand(cmdLine);
        }
        catch (Exception e)
        {
        }
        m_taskResult.put(resTag, result);
    }

    public void run(IProgressMonitor monitor) throws InterruptedException
    {
        Thread thread = new Thread(this);
        thread.start();
        while (thread.isAlive()) {
            thread.join(100);
            if (monitor.isCanceled()) {
                this.stop();
                throw new InterruptedException();
            }
        }
    }
}
