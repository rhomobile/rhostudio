package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class RunReleaseRhodesAppTask extends RhodesTask
{
    private final String       m_workDir;
    private final PlatformType m_platformType;
    private final RunType      m_runType;
    private final boolean      m_isReloadCode;
    private final boolean      m_isTrace;

    public RunReleaseRhodesAppTask(String workDir, PlatformType platformType, RunType runType,
        boolean isReloadCode, boolean isTrace)
    {
        m_workDir = workDir;
        m_platformType = platformType;
        m_runType = runType;
        m_isReloadCode = isReloadCode;
        m_isTrace = isTrace;
    }

    @Override
    protected void exec()
    {
        String task;
        if (m_runType == RunType.eDevice)
            if (m_platformType == PlatformType.eIPhone)
                task = "device:iphone:production";
            else if (m_platformType == PlatformType.eBb) // FIX for bb
                task = "device:bb:production";
            else
                task = "run:" + m_platformType + ":device";
        else if (m_runType == RunType.eRhoEmulator)
            task = "run:" + m_platformType + ":rhosimulator";
        else
            task = "run:" + m_platformType;

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(m_rakeExe);
        cmdLine.add(task);

        if (m_isTrace)
        {
            cmdLine.add("--trace");
        }

        if (m_runType == RunType.eRhoEmulator)
        {
            cmdLine.add("rho_debug_port=9000");
            cmdLine.add("rho_reload_app_changes=" + (m_isReloadCode ? "1" : "0"));
        }

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
    }
}
