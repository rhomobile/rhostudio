package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.constants.ConfigurationConstants;

public class RunReleaseRhoconnectAppTask extends RunRhoconnectAppTask 
{
	public static final String taskTag = "rhoconnect-app-release-runner";
	
	@Override
	public String getTag() 
	{
		return taskTag;
	}

	@Override
	public void run()
	{
		m_taskResult.clear();

		try 
		{
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [RunReleaseRhodesAppTask]");		

			String workDir = (String) m_taskParams.get(this.workDir);
			
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
			sb.append("rhoconnect:startbg");
			
			cmdLine = new ArrayList<String>();
			cmdLine.add(m_rakeExe);
			cmdLine.add(sb.toString());
			
			int res = m_executor.runCommand(cmdLine);
			
			Integer resCode = new Integer(res);  
			m_taskResult.put(resTag, resCode);		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
