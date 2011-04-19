package rhogenwizard.preferences;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.buildfile.YmlFile;

public class RhogenPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	PreferenceInitializer		   m_pInit = null;
	private RhogenComboFieldEditor m_selectCombo = null;
	
	public RhogenPreferencePage() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Rhodes rhobuild.yml preferences");
	}
	
	@Override
	public boolean performOk()
	{
		boolean ret = super.performOk();
		m_pInit.savePreferences();
		return ret;
	}

	private String[][] prepareComboItems()
	{
		List<String> projNames;

		projNames = PreferenceInitializer.getInstance().getRhodesProjects();

	
		if (projNames == null || projNames.size() == 0)
		{
			MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING);
			mb.setText("Warning");
			mb.setMessage("Create one or more rhodes project.");
			mb.open();
			
			return null;
		}
		
		String[][] comboItems = new String[projNames.size()][];
		
		for (int i=0; i<projNames.size(); ++i)
		{
			String ver = projNames.get(i);
			String[] newItem = {ver, ver};
			comboItems[i] = newItem;
		}
		
		return comboItems;
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() 
	{
		String[][] comboItems = prepareComboItems();
		
//		if (comboItems == null)
//		{
//			String[][] emptyComboItems = {{"",""}};
//			comboItems = emptyComboItems;
//		}
//		
//		m_selectCombo = new RhogenComboFieldEditor(PreferenceConstants.BB_VERSION, 
//							"Project:", comboItems, getFieldEditorParent());
//		m_selectCombo.setSelectionListener(new SelectionAdapter() 
//		{
//			@Override
//			public void widgetSelected(SelectionEvent e)
//			{
//				try 
//				{
//					String projectName = m_selectCombo.getCombo().getText();
//					m_pInit.initFromProject(projectName);
//				} 
//				catch (Exception e1) 
//				{
//					e1.printStackTrace();
//				}
//			}
//		});
//		addField(m_selectCombo);
		
		addField(new RhogenDirectoryFieldEditor(PreferenceConstants.JAVA_PATH, 
				"&Java path:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
		m_pInit = PreferenceInitializer.getInstance();
	}	
}