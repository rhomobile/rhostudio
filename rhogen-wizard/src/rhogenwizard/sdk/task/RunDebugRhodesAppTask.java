package rhogenwizard.sdk.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.PlatformType;
import rhogenwizard.sdk.helper.DebugConsoleAdapter;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class RunDebugRhodesAppTask extends RhodesTask
{
    public static final String resProcess = "debug-process";

    private final String       m_workDir;
    private final String       m_appName;
    private final PlatformType m_platformType;
    private final boolean      m_isReloadCode;
    private final ILaunch      m_launch;
    private final boolean      m_isTrace;

    public RunDebugRhodesAppTask(String workDir, String appName, PlatformType platformType,
        boolean isReloadCode, ILaunch launch, boolean isTrace)
    {
        m_workDir = workDir;
        m_appName = appName;
        m_platformType = platformType;
        m_isReloadCode = isReloadCode;
        m_launch = launch;
        m_isTrace = isTrace;
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(m_rakeExe);
        cmdLine.add("run:" + m_platformType + ":rhosimulator_debug");

        if (m_isTrace)
        {
            cmdLine.add("--trace");
        }

        cmdLine.add("rho_debug_port=9000");
        cmdLine.add("rho_reload_app_changes=" + (m_isReloadCode ? "1" : "0"));

        String[] commandLine = cmdLine.toArray(new String[0]);

        m_taskResult.clear();

        int result = TaskResultConverter.failCode;
        IProcess debugProcess = null;

        try
        {
            Process process;
            try
            {
                process = DebugPlugin.exec(commandLine, new File(m_workDir));
            }
            catch (CoreException e)
            {
                return;
            }

            debugProcess = DebugPlugin.newProcess(m_launch, process, m_appName);

            new DebugConsoleAdapter(debugProcess);

            result = (debugProcess == null) ? 1 : 0;
        }
        finally
        {
            m_taskResult.put(resTag, result);
            m_taskResult.put(resProcess, debugProcess);
        }
    }
}
