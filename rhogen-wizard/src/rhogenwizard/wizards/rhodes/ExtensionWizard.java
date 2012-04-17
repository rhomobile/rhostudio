package rhogenwizard.wizards.rhodes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import rhogenwizard.RunExeHelper;
import rhogenwizard.ShowMessageJob;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.CommonConstants;
import rhogenwizard.constants.UiConstants;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.project.extension.AlredyCreatedException;
import rhogenwizard.project.extension.ProjectNotFoundException;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhodesExtensionTask;
import rhogenwizard.wizards.ZeroPage;

public class ExtensionWizard extends Wizard implements INewWizard 
{
	private ExtensionWizardPage m_pageExp = null;
	private ISelection          m_selection = null;
	private IProject            m_currentProject = null;
	
	/**
	 * Constructor for SampleNewWizard.
	 */
	public ExtensionWizard() 
	{
		super();
		setNeedsProgressMonitor(true);
		
		m_currentProject = ProjectFactory.getInstance().getSelectedProject();
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() 
	{
		if (m_currentProject != null)
		{
			if (!RhodesProject.checkNature(m_currentProject) && !RhoelementsProject.checkNature(m_currentProject))
			{
				ZeroPage zeroPage = new ZeroPage("Project " + m_currentProject.getName() + " is not rhodes application");
				addPage(zeroPage);
			}
			else
			{
				m_pageExp = new ExtensionWizardPage(m_selection);
				addPage(m_pageExp);
			}
		}
		else
		{
			ZeroPage zeroPage = new ZeroPage("Select rhodes project for create model");
			addPage(zeroPage);			
		}
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() 
	{
		if (!RhodesProject.checkNature(m_currentProject) && !RhoelementsProject.checkNature(m_currentProject))
			return true;

		final String extName = m_pageExp.getExtName();
		
		IRunnableWithProgress op = new IRunnableWithProgress() 
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException 
			{
				try
				{
					doFinish(extName, monitor);
				}
				catch (CoreException e) 
				{
					throw new InvocationTargetException(e);
				} 
				catch (ProjectNotFoundException e) 
				{
					e.printStackTrace();
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
	
	private void createProjectFiles(final String extName, IProgressMonitor monitor) throws Exception
	{
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put(GenerateRhodesExtensionTask.extName, extName);
		params.put(GenerateRhodesExtensionTask.workDir, m_currentProject.getLocation().toOSString());
		
		Map results = RhoTaskHolder.getInstance().runTask(GenerateRhodesExtensionTask.class, params);
		
		if (TaskResultConverter.getResultIntCode(results) != 0)
		{
			throw new IOException("The Rhodes SDK do not installed");
		}	
	}
	
	/**
	 * @throws ProjectNotFoundExtension 
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 * @throws  
	 */
	private void doFinish(final String extName, IProgressMonitor monitor) throws CoreException, ProjectNotFoundException 
	{
		try 
		{
			monitor.beginTask("Creating " + extName + " extesion", 2);
			monitor.worked(1);
			monitor.setTaskName("Create project...");
			
			createProjectFiles(extName, monitor);
			
			if (CommonConstants.checkRhodesVersion)
			{
				monitor.setTaskName("Check Rhodes version...");
				
				try
				{
					if (!RunExeHelper.checkRhodesVersion(CommonConstants.rhodesVersion))
					{
						throw new IOException();
					}
				}
				catch (IOException e)
				{
					ShowMessageJob msgJob = new ShowMessageJob("", "Error", "Installed Rhodes have old version, need rhodes version equal or greater " 
							+ CommonConstants.rhodesVersion + " Please reinstall it (See 'http://docs.rhomobile.com/rhodes/install' for more information)");
					msgJob.run(monitor);
					return;					
				}
			}	

			m_currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			ShowPerspectiveJob job = new ShowPerspectiveJob("show rhodes perspective", UiConstants.rhodesPerspectiveId);
			job.schedule();
			
			monitor.worked(1);
		} 
		catch (IOException e)
		{
			ShowMessageJob msgJob = new ShowMessageJob("", "Error", "Cannot find Rhodes, need rhodes version equal or greater " 
					+ CommonConstants.rhodesVersion + " (See 'http://docs.rhomobile.com/rhodes/install' for more information)");
			msgJob.run(monitor);
		}
		catch (AlredyCreatedException e)
		{
			ShowMessageJob msgJob = new ShowMessageJob("", "Warining", e.toString());
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
		this.m_selection = selection;
	}
}