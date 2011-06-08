package rhogenwizard.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import rhogenwizard.AlredyCreatedException;
import rhogenwizard.BuildInfoHolder;
import rhogenwizard.CheckProjectException;
import rhogenwizard.RhodesAdapter;
import rhogenwizard.RhodesProjectSupport;
import rhogenwizard.RunExeHelper;
import rhogenwizard.ShowMessageJob;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.CommonConstants;
import rhogenwizard.constants.MsgConstants;
import rhogenwizard.constants.UiConstants;

public class RhogenSourceAdapterWizard extends Wizard implements INewWizard 
{
	private static final String okRhodesVersionFlag = "1";
	
	private RhodesSourceAdapterWizardPage m_pageApp = null;
	private ISelection                    selection = null;
	private RhodesAdapter                 m_rhogenAdapter = new RhodesAdapter();
	private IProject                      m_currentProject = null;
	private String                        m_projectLocation = null;
	
	/**
	 * Constructor for SampleNewWizard.
	 */
	public RhogenSourceAdapterWizard()
	{
		super();
		setNeedsProgressMonitor(true);
		
		m_currentProject = RhodesProjectSupport.getSelectedProject();
		
		if (m_currentProject != null)
		{
			m_projectLocation = m_currentProject.getLocation().toOSString();
		}
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() 
	{
		m_pageApp = new RhodesSourceAdapterWizardPage(selection);
		addPage(m_pageApp);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() 
	{
		final String srcAdapterName = m_pageApp.getAdapterName();
		
		IRunnableWithProgress op = new IRunnableWithProgress() 
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException 
			{
				try
				{
					doFinish(srcAdapterName, monitor);
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
		String adapterName,
		IProgressMonitor monitor)
		throws CoreException 
	{
		IProject newProject = null;
		
		try 
		{
			if (m_currentProject.isOpen())
			{
				monitor.beginTask("Creating " + m_currentProject.getName(), 2);
				monitor.worked(1);
				monitor.setTaskName("Opening file for editing...");
				
				if (m_rhogenAdapter.generateSyncAdapterApp(adapterName, m_projectLocation) != 0)
				{
					throw new IOException(MsgConstants.errInstallRhosync);
				}
	
				m_currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	
				ShowPerspectiveJob job = new ShowPerspectiveJob("show rhodes perspective", UiConstants.rhodesPerspectiveId);
				job.run(monitor);
			}
			
			monitor.worked(1);
		} 
		catch (IOException e)
		{
			ShowMessageJob msgJob = new ShowMessageJob("", "Error", MsgConstants.errFindRhosync);
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