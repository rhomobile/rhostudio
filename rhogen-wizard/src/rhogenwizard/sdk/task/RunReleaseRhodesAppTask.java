package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class RunReleaseRhodesAppTask extends RhodesTask
{
    public static final String runType      = "run-type";     // sim, rhosim,
                                                               // device
    public static final String platformType = "platform-type"; // wm, wp,
                                                               // iphone, etc
    public static final String reloadCode   = "reload-code";
    public static final String debugPort    = "debug-port";
    public static final String traceFlag    = "trace";

    public RunReleaseRhodesAppTask(String workDir, PlatformType platformType, RunType runType,
        boolean isReloadCode, boolean isTrace)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RunTask.workDir, workDir);
        params.put(RunReleaseRhodesAppTask.platformType, platformType);
        params.put(RunReleaseRhodesAppTask.runType, runType);
        params.put(RunReleaseRhodesAppTask.reloadCode, isReloadCode);
        params.put(RunReleaseRhodesAppTask.traceFlag, isTrace);
        m_taskParams = params;
    }

    @Override
    protected void exec()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException("parameters data is invalid [RunReleaseRhodesAppTask]");

        String workDir = (String) m_taskParams.get(this.workDir);
        PlatformType platformType = (PlatformType) m_taskParams.get(this.platformType);
        RunType runType = (RunType) m_taskParams.get(this.runType);
        Boolean isReloadCode = (Boolean) m_taskParams.get(this.reloadCode);
        Boolean isTrace = (Boolean) m_taskParams.get(this.traceFlag);

        String task;
        if (runType == RunType.eDevice)
            if (platformType == PlatformType.eIPhone)
                task = "device:iphone:production";
            else if (platformType == PlatformType.eBb) // FIX for bb
                task = "device:bb:production";
            else
                task = "run:" + platformType + ":device";
        else if (runType == RunType.eRhoEmulator)
            task = "run:" + platformType + ":rhosimulator";
        else
            task = "run:" + platformType;

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(m_rakeExe);
        cmdLine.add(task);

        if (isTrace)
        {
            cmdLine.add("--trace");
        }

        if (runType == RunType.eRhoEmulator)
        {
            cmdLine.add("rho_debug_port=9000");
            cmdLine.add("rho_reload_app_changes=" + (isReloadCode ? "1" : "0"));
        }

        m_taskResult.clear();

        int result = TaskResultConverter.failCode;

        try
        {
            m_executor.setWorkingDirectory(workDir);
            result = m_executor.runCommand(cmdLine);
        }
        catch (Exception e)
        {
        }

        m_taskResult.put(resTag, result);
    }
}
