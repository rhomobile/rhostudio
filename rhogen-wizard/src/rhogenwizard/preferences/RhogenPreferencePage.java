package rhogenwizard.preferences;

import java.util.List;

import org.eclipse.ui.IWorkbench;
import rhogenwizard.Activator;
import rhogenwizard.constants.MsgConstants;

public class RhogenPreferencePage extends BasePreferencePage 
{
	PreferenceInitializer		   m_pInit = null;
	private RhogenComboFieldEditor m_selectCombo = null;
	
	public RhogenPreferencePage() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(MsgConstants.preferencesPageTitle);
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
		checkRhodesSdk();
		
		List<String> projNames = PreferenceInitializer.getInstance().getRhodesProjects();
		
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
		
		addField(new RhogenDirectoryFieldEditor(PreferenceConstants.javaPath, 
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