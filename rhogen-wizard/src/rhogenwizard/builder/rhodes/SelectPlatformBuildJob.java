package rhogenwizard.builder.rhodes;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import rhogenwizard.Activator;
import rhogenwizard.PlatformType;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.task.BuildPlatformTask;

public class SelectPlatformBuildJob extends UIJob 
{
	private PlatformType m_selectPlatform = PlatformType.eUnknown;
	private String       m_workDir = null;
	
	public SelectPlatformBuildJob(String name, String workDir) 
	{
		super(name);
		
		m_workDir = workDir;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) 
	{
		Shell windowShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		SelectPlatformDialog selectDlg = new SelectPlatformDialog(windowShell);
		m_selectPlatform = selectDlg.open();
		
		if (m_selectPlatform == PlatformType.eUnknown)
			return new Status(NONE, Activator.PLUGIN_ID, "select platform");
			
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(BuildPlatformTask.workDir, m_workDir);
		params.put(BuildPlatformTask.platformType, m_selectPlatform);

		RhoTaskHolder.getInstance().runTask(BuildPlatformTask.class, params);

		return new Status(BUILD, Activator.PLUGIN_ID, "select platform");
	}
	
	public PlatformType getSelectedPlatform()
	{
		return m_selectPlatform;
	}
}
