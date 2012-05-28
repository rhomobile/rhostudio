package rhogenwizard.sdk.task;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.constants.ConfigurationConstants;

public class RunDebugRhoconnectAppTask extends SeqRunTask
{
    public static final String resProcess = "debug-process";

    private static RunTask[] getTasks(final String workDir_, final String appName, final ILaunch launch)
    {
        RunTask storeLastSyncRunAppTask = new RunTask()
        {
            @Override
            public Map<String, ?> getResult()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void run(IProgressMonitor monitor)
            {
                IPreferenceStore store = Activator.getDefault().getPreferenceStore();
                store.setValue(ConfigurationConstants.lastSyncRunApp, workDir_);
            }
        };

        RunTask redisStartbgTask = new RakeTask()
        {
            @Override
            protected void exec()
            {
                List<String> cmdLine = Arrays.asList(m_rakeExe, "redis:startbg");

                try
                {
                    m_executor.setWorkingDirectory(workDir_);
                    m_executor.runCommand(cmdLine);
                }
                catch (Exception e)
                {
                }
            }
        };

        RunTask rhoconnectStartdebugTask = new RakeTask()
        {
            @Override
            protected void exec()
            {
                String[] commandLine = { m_rakeExe, "rhoconnect:startdebug" };

                Process process;
                try
                {
                    process = DebugPlugin.exec(commandLine, new File(workDir_));
                }
                catch (CoreException e)
                {
                    m_taskResult.put(resTag, 0);
                    return;
                }

                IProcess debugProcess = DebugPlugin.newProcess(launch, process, appName);

                int resCode = (debugProcess == null) ? 0 : 1;

                m_taskResult.put(resTag, resCode);
                m_taskResult.put(resProcess, debugProcess);
            }
        };

        return new RunTask[] { new StopSyncAppTask(), storeLastSyncRunAppTask, redisStartbgTask,
            rhoconnectStartdebugTask };
    }

    public RunDebugRhoconnectAppTask(String workDir, String appName, ILaunch launch)
    {
        super(getTasks(workDir, appName, launch));
    }
}
