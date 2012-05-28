package rhogenwizard.sdk.task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.PlatformType;
import rhogenwizard.sdk.helper.DebugConsoleAdapter;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class RunDebugRhodesAppTask extends RhodesTask
{
    public static final String appName      = "app-name";
    public static final String platformType = "platform-type"; // wm, wp,
                                                               // iphone,
                                                               // etc
    public static final String reloadCode   = "reload-code";
    public static final String launchObj    = "launch";
    public static final String resProcess   = "debug-process";
    public static final String traceFlag    = "trace";

    public RunDebugRhodesAppTask(String workDir, String appName, PlatformType platformType,
        boolean isReloadCode, ILaunch launch, boolean isTrace)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RunTask.workDir, workDir);
        params.put(RunDebugRhodesAppTask.appName, appName);
        params.put(RunDebugRhodesAppTask.platformType, platformType);
        params.put(RunDebugRhodesAppTask.reloadCode, isReloadCode);
        params.put(RunDebugRhodesAppTask.launchObj, launch);
        params.put(RunDebugRhodesAppTask.traceFlag, isTrace);
        m_taskParams = params;
    }

    @Override
    protected void exec()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException("parameters data is invalid [RunDebugRhodesAppTask]");

        String workDir = (String) m_taskParams.get(RunTask.workDir);
        String appName = (String) m_taskParams.get(RunDebugRhodesAppTask.appName);
        PlatformType platformType = (PlatformType) m_taskParams.get(RunDebugRhodesAppTask.platformType);
        Boolean isReloadCode = (Boolean) m_taskParams.get(RunDebugRhodesAppTask.reloadCode);
        ILaunch launch = (ILaunch) m_taskParams.get(RunDebugRhodesAppTask.launchObj);
        Boolean isTrace = (Boolean) m_taskParams.get(RunDebugRhodesAppTask.traceFlag);

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(m_rakeExe);
        cmdLine.add("run:" + platformType + ":rhosimulator_debug");

        if (isTrace)
        {
            cmdLine.add("--trace");
        }

        cmdLine.add("rho_debug_port=9000");
        cmdLine.add("rho_reload_app_changes=" + (isReloadCode ? "1" : "0"));

        String[] commandLine = cmdLine.toArray(new String[0]);

        m_taskResult.clear();

        int result = TaskResultConverter.failCode;
        IProcess debugProcess = null;

        try
        {
            Process process;
            try
            {
                process = DebugPlugin.exec(commandLine, new File(workDir));
            }
            catch (CoreException e)
            {
                return;
            }

            debugProcess = DebugPlugin.newProcess(launch, process, appName);

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
