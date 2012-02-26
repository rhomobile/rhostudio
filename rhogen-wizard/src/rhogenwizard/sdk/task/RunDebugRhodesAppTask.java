package rhogenwizard.sdk.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.PlatformType;
import rhogenwizard.sdk.helper.DebugConsoleAdapter;

public class RunDebugRhodesAppTask extends RhodesTask
{
	public static final String taskTag      = "rhodes-app-debug-runner";
	public static final String appName      = "app-name";
	public static final String platformType = "platform-type"; // wm, wp, iphone, etc
	public static final String reloadCode   = "reload-code";
	public static final String debugPort    = "debug-port"; 
	public static final String launchObj    = "launch";
	public static final String resProcess   = "debug-process";
	public static final String traceFlag    = "trace";
	
	DebugConsoleAdapter m_dbgConsoleAdapter = null;
	
	@Override
	public void run() 
	{
		m_taskResult.clear();
		
		try 
		{			
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [RunDebugRhodesAppTask]");		
			
			String       workDir      = (String) m_taskParams.get(IRunTask.workDir);
			String       appName      = (String) m_taskParams.get(RunDebugRhodesAppTask.appName);
			PlatformType platformType = (PlatformType) m_taskParams.get(RunDebugRhodesAppTask.platformType);
			Boolean      isReloadCode = (Boolean) m_taskParams.get(RunDebugRhodesAppTask.reloadCode);
			ILaunch      launch       = (ILaunch) m_taskParams.get(RunDebugRhodesAppTask.launchObj);
			Boolean      isTrace      = (Boolean) m_taskParams.get(RunDebugRhodesAppTask.traceFlag);
			
			StringBuilder sb = new StringBuilder();
			sb.append("run:");
			sb.append(platformType.toString());
			sb.append(":rhosimulator_debug");
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(m_rakeExe);
			cmdLine.add(sb.toString());
			
			if (isTrace) {
				cmdLine.add("--trace");
			}

			cmdLine.add("rho_debug_port=9000");
			
			if (isReloadCode.booleanValue())
				cmdLine.add("rho_reload_app_changes=1");
			else
				cmdLine.add("rho_reload_app_changes=0");
			
			String[] commandLine = new String[cmdLine.size()];
			commandLine = cmdLine.toArray(commandLine);
			
			Process process = DebugPlugin.exec(commandLine, new File(workDir));

			IProcess debugProcess = DebugPlugin.newProcess(launch, process, appName);
			
			m_dbgConsoleAdapter = new DebugConsoleAdapter(debugProcess);
			
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
			e.printStackTrace();
		}
	}

	@Override
	public String getTag() 
	{
		return taskTag;
	}
}
