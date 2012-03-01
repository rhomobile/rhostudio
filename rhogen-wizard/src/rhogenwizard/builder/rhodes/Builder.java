package rhogenwizard.builder.rhodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.PlatformType;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.task.BuildPlatformTask;
import rhogenwizard.sdk.task.CleanAllPlatfromTask;
import rhogenwizard.sdk.task.CompileRubyPartTask;

class SelectPlatformJob extends UIJob 
{
	private PlatformType m_selectPlatform = PlatformType.eUnknown;
	private String       m_workDir = null;
	
	public SelectPlatformJob(String name, String workDir) 
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

public class Builder extends IncrementalProjectBuilder 
{
	public  static final String BUILDER_ID = "com.rhomobile.rhostudio.rhogenBuilder";

	public Builder()
	{
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, final IProgressMonitor monitor) throws CoreException 
	{
		fullBuild(monitor);
		
		SelectPlatformJob buildJob = new SelectPlatformJob("select platform", getProject().getLocation().toOSString());
		buildJob.run(monitor);
		
		return null;
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException 
	{
		try 
		{
			ConsoleHelper.consoleBuildPrint("Clean project started");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put(CleanAllPlatfromTask.workDir, getProject().getLocation().toOSString());

			RhoTaskHolder.getInstance().runTask(CleanAllPlatfromTask.class, params);
			
			ConsoleHelper.consoleBuildPrint("Clean application cancelled");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		super.clean(monitor);
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException 
	{
		try 
		{
			List<String> out = compileRubyPart();
			getProject().accept(new ResourceVisitor(out));
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	private List<String> compileRubyPart()
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(CompileRubyPartTask.workDir, getProject().getLocation().toOSString());
		params.put(CompileRubyPartTask.platformType, PlatformType.eWm);
		
		Map<String, ?> res = RhoTaskHolder.getInstance().runTask(CompileRubyPartTask.class, params);
		
		List<String> errLines = (List<String>) res.get(CompileRubyPartTask.outStrings);

		return errLines;
	}
}
