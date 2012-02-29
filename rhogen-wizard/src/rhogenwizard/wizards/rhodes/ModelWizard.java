package rhogenwizard.wizards.rhodes;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.*;
import org.eclipse.ui.progress.UIJob;

import rhogenwizard.Activator;
import rhogenwizard.OSHelper;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhodesAppTask;
import rhogenwizard.sdk.task.GenerateRhodesModelTask;
import rhogenwizard.wizards.ZeroPage;

class ModelCreationJob extends UIJob 
{
	String   m_modelName = null;
	String   m_modelParams = null;
	String   m_projectLocation = null;
	IProject m_currentProject = null;
	
	public ModelCreationJob(String name
							, String projectLocation
							, String modelName
							, String modelParams
							, IProject currentProject) 
	{
		super(name);
		m_modelName       = modelName;
		m_modelParams     = modelParams;
		m_projectLocation = projectLocation;
		m_currentProject  = currentProject;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		Shell windowShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		File modelFolder = new File(m_projectLocation + "/app/" + m_modelName);
		
		if (modelFolder.exists())
		{
			MessageBox messageBox = new MessageBox(windowShell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			messageBox.setText("Model create");
			messageBox.setMessage("Model with name " + m_modelName + " was already created. Delete old model?");
			
			if (SWT.OK == messageBox.open())
			{
				OSHelper.deleteFolder(modelFolder);
			}
			else
			{
				return new Status(BUILD, Activator.PLUGIN_ID, "model create");
			}
		}
		
		try
		{
			Map<String, Object> params = new HashMap<String, Object>();
			
			params.put(GenerateRhodesModelTask.modelName, m_modelName);
			params.put(GenerateRhodesModelTask.workDir, m_projectLocation);
			params.put(GenerateRhodesModelTask.modelFields, m_modelParams);
			
			Map results = RhoTaskHolder.getInstance().runTask(GenerateRhodesModelTask.class, params);
			
			if (TaskResultConverter.getResultIntCode(results) != 0)
			{
				throw new IOException("The Rhodes SDK do not installed");
			}
			
			m_currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return new Status(BUILD, Activator.PLUGIN_ID, "model create");
	}
}

public class ModelWizard extends Wizard implements INewWizard 
{
	private ModelWizardPage m_pageModel = null;
	private ISelection      m_selection = null;
	private String 	        m_projectLocation = null;
	private IProject		m_currentProject = null;
	
	public ModelWizard()
	{
		super();
		setNeedsProgressMonitor(true);
		
		m_currentProject = ProjectFactory.getInstance().getSelectedProject();

		if (m_currentProject != null)
		{	
			m_projectLocation = m_currentProject.getLocation().toOSString();
		}
	}
	
	/**
	 * Constructor for SampleNewWizard.
	 */
	public ModelWizard(IProject currentProject) 
	{
		super();
		setNeedsProgressMonitor(true);
		
		m_currentProject  = currentProject;
		m_projectLocation = m_currentProject.getLocation().toOSString();
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() 
	{
		if (m_currentProject != null)
		{
			if (!RhodesProject.checkNature(m_currentProject))
			{
				ZeroPage zeroPage = new ZeroPage("Project " + m_currentProject.getName() + " is not rhodes application");
				addPage(zeroPage);
			}
			else
			{
				m_pageModel = new ModelWizardPage(m_selection);
				addPage(m_pageModel);
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
		if (!RhodesProject.checkNature(m_currentProject))
			return true;
	
		final String modelName   = m_pageModel.getModelName();
		final String modelParams = m_pageModel.getModelParams();
		
		IRunnableWithProgress op = new IRunnableWithProgress() 
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException 
			{
				try
				{
					doFinish(modelName, modelParams, monitor);
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
		String modelName,
		String modelParams,
		IProgressMonitor monitor)
		throws CoreException 
	{
		try 
		{
			monitor.beginTask("Creating model " + modelName, 2);
			monitor.worked(1);
			monitor.setTaskName("Creating model...");
			
			if (null != m_projectLocation)
			{
				ModelCreationJob modelJob = new ModelCreationJob("create model", m_projectLocation, 
						modelName, modelParams, m_currentProject);
				modelJob.run(monitor);
			}
			else
			{
				//TODO show error message
			}
			
			monitor.worked(1);
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