package rhogenwizard;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IStartup;

import rhogenwizard.preferences.PreferenceInitializer;
import rhogenwizard.project.IRhomobileProject;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.project.extension.BadProjectTagException;
import rhogenwizard.project.extension.ProjectNotFoundException;
import rhogenwizard.rhohub.TokenChecker;

public class StartupImpl implements IStartup 
{
	private static class UpdateRhomobileProject
	{
		private static int refreshDelay = 1;
		
		private static void deferredRefresh(final IProject project)
		{
			final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
			
			Runnable task = new Runnable() 
			{
				public void run() 
				{
					UpdateRhomobileProject.update(project);
				}
			};
			
			worker.schedule(task, refreshDelay, TimeUnit.SECONDS);
		}

		public static void update(IProject project)
		{
			try 
			{
				if (RhodesProject.checkNature(project) || RhoelementsProject.checkNature(project))
				{
					IRhomobileProject rhoProject = ProjectFactory.getInstance().convertFromProject(project);
					rhoProject.refreshProject();
				}
			}
			catch (ResourceException e)
			{
				deferredRefresh(project);
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
	
	private static class ResourceChangeListener implements IResourceChangeListener
	{
		@Override
		public void resourceChanged(IResourceChangeEvent event) 
		{
			if (event.getType() != IResourceChangeEvent.POST_CHANGE)
				return;
			
			IResourceDelta   mainDelta = event.getDelta();			
			IResourceDelta[] resDeltas = mainDelta.getAffectedChildren(IResourceDelta.ADDED);
			
			for (IResourceDelta resDelta : resDeltas)
			{
				IResource resource = resDelta.getResource();
				
				if (resource instanceof IProject)
				{
					UpdateRhomobileProject.update((IProject) resource);
				}	
			}
		}
	}
	
	@Override
	public void earlyStartup() 
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceChangeListener(), IResourceChangeEvent.POST_CHANGE);

		TokenChecker.processToken();
	}
}

























