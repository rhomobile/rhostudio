package rhogenwizard.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
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
import org.eclipse.ui.ide.IDE;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.URIUtil;

import rhogenwizard.AlredyCreatedException;
import rhogenwizard.BuildInfoHolder;
import rhogenwizard.RhodesAdapter;
import rhogenwizard.RhodesProjectSupport;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.debugger.RhogenConstants;

public class RhogenAppWizard extends Wizard implements INewWizard 
{
	private RhodesAppWizardPage  m_pageApp = null;
	private ISelection           selection = null;
	private RhodesAdapter        m_rhogenAdapter = new RhodesAdapter();
	
	/**
	 * Constructor for SampleNewWizard.
	 */
	public RhogenAppWizard() 
	{
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() 
	{
		m_pageApp = new RhodesAppWizardPage(selection);
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
		BuildInfoHolder infoHolder,
		IProgressMonitor monitor)
		throws CoreException 
	{
		try 
		{
			monitor.beginTask("Creating " + infoHolder.appName, 2);
			monitor.worked(1);
			monitor.setTaskName("Opening file for editing...");

			m_rhogenAdapter.generateApp(infoHolder);
			
			RhodesProjectSupport.createProject(infoHolder);
			
			ShowPerspectiveJob job = new ShowPerspectiveJob("show rhodes perspective", RhogenConstants.rhodesPerspectiveId);
			job.run(monitor);
			
			monitor.worked(1);
		} 
		catch (AlredyCreatedException e)
		{
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Warining");
			messageBox.setMessage(e.getMessage());
			messageBox.open();			
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
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}