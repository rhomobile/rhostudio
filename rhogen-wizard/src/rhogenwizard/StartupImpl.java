package rhogenwizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IStartup;

import rhogenwizard.project.IRhomobileProject;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.project.extension.BadProjectTagException;
import rhogenwizard.project.extension.ProjectNotFoundException;
import rhogenwizard.sdk.task.RunTask;

public class StartupImpl implements IStartup 
{
	private static class UpdateRhoProjectsTask extends RunTask
	{
		@Override
		public boolean isOk() 
		{
			return true;
		}

		@Override
		public void run(IProgressMonitor monitor) 
		{
			IProject[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			
			for (IProject project : workspaceProjects) 
			{
				try 
				{
					if (RhodesProject.checkNature(project) || RhoelementsProject.checkNature(project))
					{
						IRhomobileProject rhoProject = ProjectFactory.getInstance().convertFromProject(project);
						rhoProject.refreshProject();
					}
				}
				catch (BadProjectTagException e) 
				{
					e.printStackTrace();
				}
				catch (ProjectNotFoundException e) 
				{
					e.printStackTrace();
				} 
				catch (CoreException e) 
				{
					e.printStackTrace();
				}
			}
		}		
	}
	
	@Override
	public void earlyStartup() 
	{
		UpdateRhoProjectsTask task = new UpdateRhoProjectsTask();
		Job refreshJob = task.makeJob("Refresh Rhodes and Rhoelements project");
		refreshJob.schedule();
	}
}
