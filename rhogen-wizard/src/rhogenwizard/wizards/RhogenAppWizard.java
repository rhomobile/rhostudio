package rhogenwizard.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
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

import rhogenwizard.CustomProjectSupport;
import rhogenwizard.RhodesAdapter;

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
		final String appFolder = m_pageApp.getAppFolder();
		final String appName = m_pageApp.getAppName();
	
		IRunnableWithProgress op = new IRunnableWithProgress() 
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException 
			{
				try
				{
					doFinish(appFolder, appName, monitor);
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
		String containerName,
		String appName,
		IProgressMonitor monitor)
		throws CoreException 
	{
		try 
		{
			monitor.beginTask("Creating " + appName, 2);
			monitor.worked(1);
			monitor.setTaskName("Opening file for editing...");

			m_rhogenAdapter.generateApp(containerName, appName);
			
			
			CustomProjectSupport.createProject(appName, URIUtil.toURI(new Path(containerName)));
			


/*
			getShell().getDisplay().asyncExec(new Runnable() 
			{	
				@Override
				public void run() 
				{
					File fileToOpen = new File("C:\\Android\\asd.txt");
					 
					if (fileToOpen.exists() && fileToOpen.isFile()) 
					{
					    IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
					    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					 
					    try {
					        IDE.openEditorOnFileStore( page, fileStore );
					    } 
					    catch ( PartInitException e ) {
					        //Put your exception handler here if you wish to
					    }
					} else {
					    //Do something if the file does not exist
					}	
				}
			});
			*/
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
		this.selection = selection;
	}
}