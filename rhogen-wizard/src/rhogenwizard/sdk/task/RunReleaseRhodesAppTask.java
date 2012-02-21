package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;

public class RunReleaseRhodesAppTask extends RhodesTask
{
	public static final String taskTag      = "rhodes-app-release-runner";
	public static final String runType      = "run-type"; // sim, rhosim, device
	public static final String platformType = "platform-type"; // wm, wp, iphone, etc
	public static final String reloadCode   = "reload-code";
	public static final String debugPort    = "debug-port";
	public static final String traceFlag    = "trace"; 

	@Override
	public void run() 
	{
		m_taskResult.clear();
		
		try 
		{			
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [RunReleaseRhodesAppTask]");		
			
			String       workDir      = (String) m_taskParams.get(this.workDir);
			PlatformType platformType = (PlatformType) m_taskParams.get(this.platformType);
			RunType      runType      = (RunType) m_taskParams.get(this.runType);
			Boolean      isReloadCode = (Boolean) m_taskParams.get(this.reloadCode);
			Boolean      isTrace      = (Boolean) m_taskParams.get(this.traceFlag);
			
			StringBuilder sb = new StringBuilder();
			sb.append("run:");
			sb.append(platformType.toString());
			
			if (runType == RunType.eDevice)
			{
				if (platformType == PlatformType.eIPhone)
				{
					sb = new StringBuilder();
					sb.append("device:iphone:production");
				}
				else if (platformType == PlatformType.eBb) // FIX for bb
				{
					sb = new StringBuilder();
					sb.append("device:bb:production");
				}
				else
					sb.append(":device");
			}
			else if (runType == RunType.eRhoEmulator)
			{
				sb.append(":rhosimulator");
			}
			
			m_executor.setWorkingDirectory(workDir);
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(m_rakeExe);
			cmdLine.add(sb.toString());
			
			if (isTrace)
			{
				cmdLine.add("--trace");
			}
			
			if (runType == RunType.eRhoEmulator)
			{
				cmdLine.add("rho_debug_port=9000");
				
				if (isReloadCode.booleanValue())
					cmdLine.add("rho_reload_app_changes=1");
				else
					cmdLine.add("rho_reload_app_changes=0");
			}
			
			int res = m_executor.runCommand(cmdLine);
		
			Integer resCode = new Integer(res);  
			m_taskResult.put(resTag, resCode);
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
