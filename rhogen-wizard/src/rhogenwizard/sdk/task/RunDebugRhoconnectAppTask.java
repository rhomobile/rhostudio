package rhogenwizard.sdk.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.PlatformType;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class RunDebugRhoconnectAppTask extends RunRhoconnectAppTask
{
	public static final String taskTag = "rhoconnect-app-debug-runner";
	
	public static final String launchObj  = "launch";
	public static final String appName    = "app-name";
	public static final String resProcess = "debug-process";

	@Override
	public void run() 
	{
		try 
		{
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [RunDebugRhoconnectAppTask]");		
			
			String       workDir      = (String) m_taskParams.get(this.workDir);
			String       appName      = (String) m_taskParams.get(this.appName);
			ILaunch      launch       = (ILaunch) m_taskParams.get(this.launchObj);
		
			stopSyncApp();
		
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		    store.setValue(ConfigurationConstants.lastSyncRunApp, workDir);
	
			StringBuilder sb = new StringBuilder();
			sb.append("redis:startbg");
			
			m_executor.setWorkingDirectory(workDir);
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(m_rakeExe);
			cmdLine.add(sb.toString());
			
			m_executor.runCommand(cmdLine);
			
			sb = new StringBuilder();
			sb.append("rhoconnect:startdebug");
			
			cmdLine = new ArrayList<String>();
			cmdLine.add(m_rakeExe);
			cmdLine.add(sb.toString());
			
			String[] commandLine = new String[cmdLine.size()];
			commandLine = cmdLine.toArray(commandLine);
			
			Process process = DebugPlugin.exec(commandLine, new File(workDir));
	
			IProcess debugProcess = DebugPlugin.newProcess(launch, process, appName);
			
			Integer resCode = null;
			
			if (debugProcess == null)
				resCode = new Integer(0);
			else 
				resCode = new Integer(1);
			
			m_taskResult.put(resTag, resCode);
			m_taskResult.put(resProcess, debugProcess);
		}
		catch (Exception e)
		{
			Integer resCode = new Integer(TaskResultConverter.failCode);  
			m_taskResult.put(resTag, resCode);		
		}
	}
}
