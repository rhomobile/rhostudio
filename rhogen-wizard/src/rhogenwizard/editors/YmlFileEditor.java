package rhogenwizard.editors;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.editors.text.TextEditor;

import rhogenwizard.PlatformType;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.project.IRhomobileProject;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.extension.BadProjectTagException;
import rhogenwizard.project.extension.ProjectNotFoundException;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class YmlFileEditor extends MultiPageEditorPart implements IResourceChangeListener
{
	private static final int labelWidht = 120;
	private static final int buttonWidht = 60;
	
	private AppYmlFile  m_ymlFile = null;
	
	private TextEditor 	m_editor;
	private Text 		m_appLogText     = null;
	private Text 		m_rhodesPathText = null;
	private Text		m_capabText	     = null;
	private Text		m_appNameText    = null;
	/**
	 * Creates a multi-page editor example.
	 */
	public YmlFileEditor() 
	{
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage0() 
	{
		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 10;
		composite.setLayout(layout);

		GridData labelAligment = new GridData();
		labelAligment.widthHint = labelWidht;
		
		GridData textAligment = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		 
		GridData buttonAligment = new GridData();
		buttonAligment.widthHint = buttonWidht;
		
		// row 0
		Label label = new Label(composite, SWT.NULL);
		label.setText("Application name:");
		label.setLayoutData(labelAligment);
		
		m_appNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_appNameText.setLayoutData(textAligment);
		m_appNameText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(composite, SWT.NULL);
		
		// row 1
		label = new Label(composite, SWT.NULL);
		label.setText("Application log file:");
		label.setLayoutData(labelAligment);
		
		m_appLogText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_appLogText.setLayoutData(textAligment);
		m_appLogText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(composite, SWT.NULL);
		
		// row 2
		label = new Label(composite, SWT.NULL);
		label.setText("Rhodes path:");
		label.setLayoutData(labelAligment);
		
		m_rhodesPathText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_rhodesPathText.setLayoutData(textAligment);
		m_rhodesPathText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.setLayoutData(buttonAligment);
		browseButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				handleBrowse();
			}
		});
		
		// row 3
		label = new Label(composite, SWT.NULL);
		label.setText("Capabilities:");
		label.setLayoutData(labelAligment);
		
		m_capabText = new Text(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		m_capabText.setLayoutData(textAligment);
		m_capabText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e)
			{
				dialogChanged();
			}
		});
		
		Button addCapabButton = new Button(composite, SWT.PUSH);
		addCapabButton.setText("Add");
		addCapabButton.setLayoutData(buttonAligment);
		addCapabButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				handleAddCapab();
			}
		});

		int index = addPage(composite);
		setPageText(index, "Rhobuild setting");
	}
	
	protected void applyChanges() 
	{
		try
		{
			m_ymlFile.save();
			
			IEditorInput editorInput = getEditorInput();
			
			if (!(editorInput instanceof IFileEditorInput))
				return;

			FileEditorInput fileEditor = (FileEditorInput)editorInput;

			m_editor.updatePartControl(fileEditor);
			
			IRhomobileProject project = ProjectFactory.getInstance().convertFromProject(fileEditor.getFile().getProject());
			project.refreshProject();
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
		catch (BadProjectTagException e) 
		{
			e.printStackTrace();
		}
		catch (ProjectNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	protected void handleAddCapab() 
	{
		CapabDialog dlg = new CapabDialog(getContainer().getShell(), m_ymlFile);
		showCapabilitiesText(dlg.open());
		applyChanges();
	}

	private void showCapabilitiesText(List<Capabilities> capList)
	{
		if (capList != null)
		{
			StringBuilder sb = new StringBuilder();
			
			for (Capabilities s : capList) 
			{
				
				sb.append(s.toString());
				sb.append(", ");
			}
			
			m_capabText.setText(sb.toString());
			
			m_ymlFile.setCapabilities(capList);
		}
	}
	
	private void dialogChanged() 
	{
		String appName = m_appNameText.getText();
		String appLog  = m_appLogText.getText();
		String sdkPath = m_rhodesPathText.getText();
		
		String sdkAppName = m_ymlFile.getAppName();
		String sdkAppLog  = m_ymlFile.getAppLog();
		String sdkSdkPath = m_ymlFile.getSdkPath();
		
		if (appName != null && appName.length() != 0)
		{
			if (!appName.equals(sdkAppName))
			{
				m_ymlFile.setAppName(appName);
				applyChanges();
			}
		}

		if (appLog != null && appLog.length() != 0)
		{
			if (!sdkAppLog.equals(appLog))
			{
				m_ymlFile.setAppLog(appLog);
				applyChanges();
			}
		}
		
		if (sdkPath != null && sdkPath.length() != 0)
		{
			if (!sdkSdkPath.equals(sdkPath))
			{
				m_ymlFile.setSdkPath(sdkPath);
				applyChanges();
			}
		}
	}

	void createPage1() 
	{
		try 
		{
			m_editor = new TextEditor();
			int index = addPage(m_editor, getEditorInput());
			setPageText(index, m_editor.getTitle());
		} 
		catch (PartInitException e) 
		{
			e.printStackTrace();
		}
	}
	
	protected void createPages() 
	{
		if (getFileName().equals(AppYmlFile.configFileName))
		{
			createPage0();
		}
		
		createPage1();
	}

	public void dispose() 
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public void doSave(IProgressMonitor monitor)
	{
		m_editor.doSave(monitor);
	}
	
	public void doSaveAs() 
	{
		doSave(null);
	}
	
	@SuppressWarnings("deprecation")
    public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException 
	{
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		
		super.init(site, editorInput);
		
		setTitle(getFileName());
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() 
	{
		return false;
	}

	protected void pageChange(int newPageIndex) 
	{
		super.pageChange(newPageIndex);
		
		try 
		{	
			if (!getFileName().equals(AppYmlFile.configFileName))
			{
				newPageIndex = 1;
			}
			
			if (newPageIndex == 0)
			{
				m_editor.doSave(null);
				
				String pathToFile = getFileLocation();
				
				m_ymlFile = null;
				m_ymlFile = new AppYmlFile(pathToFile);
				
				String sdk     = m_ymlFile.getSdkPath();
				String appName = m_ymlFile.getAppName();
				String logName = m_ymlFile.getAppLog();
				
				m_appLogText.setText(logName);
				m_rhodesPathText.setText(sdk);
				m_appNameText.setText(appName);
				
				showCapabilitiesText(m_ymlFile.getCapabilities());
			
				return;
			}	
			
			if (newPageIndex == 1)
			{
				m_editor.setInput(new FileEditorInput(getFile()));
				getFile().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		} 
	}
	
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event)
	{
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					
					for (int i = 0; i<pages.length; i++)
					{
						if(((FileEditorInput)m_editor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(m_editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
		
	private void handleBrowse() 
	{
		DirectoryDialog appDirDialog = new DirectoryDialog(this.getContainer().getShell());
		String newPath = appDirDialog.open();
		
		if (newPath != null)
			m_rhodesPathText.setText(newPath);
	}

	private IFile getFile()
	{
		IEditorInput editorInput = getEditorInput();
		
		if (!(editorInput instanceof IFileEditorInput))
			return null;

		IFileEditorInput fileEditorInput = (IFileEditorInput)editorInput;
		
		IFile currFile = fileEditorInput.getFile();
		
		return currFile;
	}
	
	private String getFileName()
	{
		IFile currFile = getFile();

		if (currFile != null)
			return currFile.getName();
		
		return null;
	}
	
	private String getFileLocation()
	{
		IEditorInput editorInput = getEditorInput();
		
		if (!(editorInput instanceof IFileEditorInput))
			return null;

		IFileEditorInput fileEditorInput = (IFileEditorInput)editorInput;
		
		IFile currFile = fileEditorInput.getFile();
		
		return currFile.getLocation().toOSString();
	}
}
