package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.sdk.helper.ConsoleAppAdapter;
import rhogenwizard.sdk.helper.TaskResultConverter;

class RhoconnectProcessRunner extends Thread
{
    private String             m_rakeExe  = "rake";
    private String             m_cmd      = null;
    private String             m_workDir  = null;

    private SysCommandExecutor m_executor = new SysCommandExecutor();

    public RhoconnectProcessRunner(final String command, final String workDir)
    {
        m_workDir = workDir;
        m_cmd = command;

        m_executor.setOutputLogDevice(new ConsoleAppAdapter());
        m_executor.setErrorLogDevice(new ConsoleAppAdapter());

        if (OSValidator.OSType.WINDOWS == OSValidator.detect())
        {
            m_rakeExe = m_rakeExe + ".bat";
        }
    }

    @Override
    public void run()
    {
        if (m_workDir == null)
            return;

        ConsoleHelper.showAppConsole();

        List<String> cmdLine = new ArrayList<String>();

        cmdLine = new ArrayList<String>();
        cmdLine.add(m_rakeExe);
        cmdLine.add(m_cmd);

        try
        {
            m_executor.setWorkingDirectory(m_workDir);
            int res = m_executor.runCommand(cmdLine);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

public class RunReleaseRhoconnectAppTask extends RhoconnectTask
{
    private static RhoconnectProcessRunner rhoconnectRunner = null;

    @Override
    protected void exec()
    {
        m_taskResult.clear();

        try
        {
            if (m_taskParams == null || m_taskParams.size() == 0)
                throw new InvalidAttributesException("parameters data is invalid [RunReleaseRhodesAppTask]");

            String workDir = (String) m_taskParams.get(this.workDir);

            new StopSyncAppTask().run();

            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
            store.setValue(ConfigurationConstants.lastSyncRunApp, workDir);

            StringBuilder sb = new StringBuilder();
            sb.append("redis:startbg");

            m_executor.setWorkingDirectory(workDir);

            List<String> cmdLine = new ArrayList<String>();
            cmdLine.add(m_rakeExe);
            cmdLine.add(sb.toString());

            m_executor.runCommand(cmdLine);

            rhoconnectRunner = new RhoconnectProcessRunner("rhoconnect:start", workDir);
            rhoconnectRunner.start();

            Integer resCode = new Integer(TaskResultConverter.okCode);
            m_taskResult.put(resTag, resCode);
        }
        catch (Exception e)
        {
            Integer resCode = new Integer(TaskResultConverter.failCode);
            m_taskResult.put(resTag, resCode);
        }
    }
}
