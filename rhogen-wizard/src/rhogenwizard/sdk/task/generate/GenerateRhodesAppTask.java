package rhogenwizard.sdk.task.generate;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.DialogUtils;
import rhogenwizard.OSHelper;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhodesAppTask extends RubyExecTask
{
	private final String m_appName;

	public GenerateRhodesAppTask(String workDir, String appName)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rhodes", "app", appName);
        
        m_appName = appName;
    }
 
	@Override
	public void run(IProgressMonitor monitor) 
	{
		if (DialogUtils.quetsion("Wrong directory", "In destination directory folder with name \'" + m_appName + "\' is exist. Delete the folder?"))
		{
			OSHelper.deleteFolder(m_workDir + File.separator + m_appName);
		}
		else
		{
			return;
		}

		super.run(monitor);
	}

	@Override
	public void run() 
	{
		if (DialogUtils.quetsion("Wrong directory", "In destination directory folder with name \'" + m_appName + "\' is exist. Delete the folder?"))
		{
			OSHelper.deleteFolder(m_workDir + File.separator + m_appName);
		}
		else
		{
			return;
		}
		
		super.run();
	}
}
