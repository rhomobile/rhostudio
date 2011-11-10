package rhogenwizard.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import rhogenwizard.AlredyCreatedException;
import rhogenwizard.BuildInfoHolder;
import rhogenwizard.CheckProjectException;
import rhogenwizard.RhodesProjectSupport;
import rhogenwizard.ShowMessageJob;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.MsgConstants;
import rhogenwizard.constants.UiConstants;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhoconnectAppTask;

public class RhoconnectAppWizard extends Wizard implements INewWizard 
{
	private static final String okRhodesVersionFlag = "1";
	
	private RhoconnectAppWizardPage m_pageApp = null;
	private ISelection        selection = null;
	
	/**
	 * Constructor for SampleNewWizard.
	 */
	public RhoconnectAppWizard() 
	{
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() 
	{
		m_pageApp = new RhoconnectAppWizardPage(selection);
		addPage(m_pageApp);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() 
	{
		final BuildInfoHolder holder = m_pageApp.getBuildInformation();
		
		IRunnableWithProgress op = new IRunnableWithProgress() 
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException 
			{
				try
				{
					doFinish(holder, monitor);
				}
				catch (CoreException e) 
				{
					throw new InvocationTargetException(e);
				} 
				finally 
				{
					monitor.done();
				}
			}
		};
		
		try 
		{
			getContainer().run(true, false, op);
		} 
		catch (InterruptedException e) 
		{
			return false;
		} 
		catch (InvocationTargetException e) 
		{
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */
	private void doFinish(
		BuildInfoHolder infoHolder,
		IProgressMonitor monitor)
		throws CoreException 
	{
		IProject newProject = null;
		
		try 
		{
			monitor.beginTask("Creating " + infoHolder.appName, 2);
			monitor.worked(1);
			monitor.setTaskName("Create project...");
			
			newProject = RhodesProjectSupport.createProject(infoHolder);
	
			if (!infoHolder.existCreate) 
			{
				monitor.setTaskName("Generate application...");
				
				Map<String, Object> params = new HashMap<String, Object>();
	
				params.put(GenerateRhoconnectAppTask.appName, infoHolder.appName);
				params.put(GenerateRhoconnectAppTask.workDir, infoHolder.getProjectLocationPath().toOSString());
				
				Map results = RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAppTask.taskTag, params);
				
				if (TaskResultConverter.getResultIntCode(results) != 0)
				{
					throw new IOException(MsgConstants.errInstallRhosync);
				}	
			}
			
			newProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						
			ShowPerspectiveJob job = new ShowPerspectiveJob("show rhodes perspective", UiConstants.rhodesPerspectiveId);
			job.run(monitor);
			
			monitor.worked(1);
		} 
		catch (IOException e)
		{
			newProject.delete(false, false, monitor);
			ShowMessageJob msgJob = new ShowMessageJob("", "Error", MsgConstants.errFindRhosync);
			msgJob.run(monitor);
		}
		catch (CheckProjectException e) 
		{
			ShowMessageJob msgJob = new ShowMessageJob("", "Error", e.getMessage());
			msgJob.run(monitor);		
		}
		catch (AlredyCreatedException e)
		{
			ShowMessageJob msgJob = new ShowMessageJob("", "Warning", e.toString());
			msgJob.run(monitor);		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.selection = selection;
	}
}