package rhogenwizard.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.views.navigator.*;

import rhogenwizard.RhodesAdapter;
import rhogenwizard.RhodesProjectSupport;

public class RhogenModelWizard extends Wizard implements INewWizard 
{
	private RhodesModelWizardPage m_pageModel = null;
	private ISelection            m_selection = null;
	private RhodesAdapter         m_rhogenAdapter = new RhodesAdapter();
	private String 	              m_projectLocation = null;
	private IProject			  m_currentProject = null;
	
	public RhogenModelWizard()
	{
		super();
		setNeedsProgressMonitor(true);
		
		m_currentProject = RhodesProjectSupport.getSelectedProject();
		m_projectLocation = m_currentProject.getLocation().toOSString();
	}
	
	/**
	 * Constructor for SampleNewWizard.
	 */
	public RhogenModelWizard(IProject currentProject) 
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
		m_pageModel = new RhodesModelWizardPage(m_selection);
		addPage(m_pageModel);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() 
	{
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
				catch (CoreException e) {
					throw new InvocationTargetException(e);
				} 
				finally {
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
			monitor.setTaskName("Opening file for editing...");
			
			if (null != m_projectLocation)
			{
				m_rhogenAdapter.generateModel(m_projectLocation, modelName, modelParams);
			}
			else
			{
				//TODO show error message
			}
			
			m_currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			
			monitor.worked(1);
		} 
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.m_selection = selection;
	}
}