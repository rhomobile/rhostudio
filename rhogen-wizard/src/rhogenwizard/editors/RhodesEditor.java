package rhogenwizard.editors;


import java.io.FileNotFoundException;
import java.util.List;

import javax.annotation.Resource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
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
import org.eclipse.ui.ide.IDE;

import rhogenwizard.buildfile.AppYmlFile;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class RhodesEditor extends MultiPageEditorPart implements IResourceChangeListener
{
	private static final int textWidth = 300;
	private static final int labelWidht = 120;
	private static final int buttonWidht = 60;
	
	private AppYmlFile  m_ymlFile = null;
	
	private TextEditor 	m_editor;
	private Text 		m_appLogText     = null;
	private Text 		m_rhodesPathText = null;
	private Text		m_capabText	     = null;
	private Text		m_appNameText    = null;
	private Button 		m_applyButton    = null;
	
	/**
	 * Creates a multi-page editor example.
	 */
	public RhodesEditor() 
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
		label.setText("Rhodes folder path:");
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
		
		m_capabText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_capabText.setLayoutData(textAligment);
		m_capabText.setEnabled(false);
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
		
		// end row
		label = new Label(composite, SWT.NULL);
		label = new Label(composite, SWT.NULL);
		
		m_applyButton = new Button(composite, SWT.PUSH);
		m_applyButton.setText("Apply");
		m_applyButton.setLayoutData(buttonAligment);
		m_applyButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				applyChanges();
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
			
			fileEditor.getFile().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			
			m_applyButton.setEnabled(false);
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	protected void handleAddCapab() 
	{
		CapabDialog dlg = new CapabDialog(getContainer().getShell(), m_ymlFile);
		showCapabilitiesText(dlg.open());
	}

	private void showCapabilitiesText(List<String> capList)
	{
		//m_applyButton.setEnabled(false);
		
		if (capList != null)
		{
			StringBuilder sb = new StringBuilder();
			
			for (String s : capList) 
			{
				sb.append(s);
				sb.append(", ");
			}
			
			m_capabText.setText(sb.toString());
			
			m_ymlFile.setCapabilities(capList);
			
			m_applyButton.setEnabled(true);
		}
	}
	
	private void dialogChanged() 
	{
		//m_applyButton.setEnabled(false);
		
		String appName = m_appNameText.getText();
		String appLog  = m_appLogText.getText();
		String sdkPath = m_rhodesPathText.getText();
		
		if (appName != null && appName.length() != 0)
		{
			m_ymlFile.setAppName(appName);
			m_applyButton.setEnabled(true);
		}
		
		if (appLog != null && appLog.length() != 0)
		{
			m_ymlFile.setAppLog(appLog);
			m_applyButton.setEnabled(true);
		}
		
		if (sdkPath != null && sdkPath.length() != 0)
		{
			m_ymlFile.setSdkPath(sdkPath);
			m_applyButton.setEnabled(true);
		}
	}

	/**
	 * Creates page 1 of the multi-page editor,
	 * which allows you to change the font used in page 2.
	 */
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
	
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() 
	{
		createPage0();
		createPage1();
	}
	
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() 
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor)
	{
	}
	
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() 
	{
		doSave(null);
	}
	
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException 
	{
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		
		super.init(site, editorInput);
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() 
	{
		return false;
	}
	
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) 
	{
		super.pageChange(newPageIndex);
		
		try 
		{	
			if (newPageIndex == 0)
			{
				String pathToFile = getFileLocation();
				
				m_ymlFile = new AppYmlFile(pathToFile);
				
				m_appLogText.setText(m_ymlFile.getAppLog());
				m_rhodesPathText.setText(m_ymlFile.getSdkPath());
				m_appNameText.setText(m_ymlFile.getAppName());
				
				showCapabilitiesText(m_ymlFile.getCapabilities());
				
				m_applyButton.setEnabled(false);
				
				return;
			}	
		} 
		catch (FileNotFoundException e) 
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
