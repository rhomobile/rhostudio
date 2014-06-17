package rhogenwizard.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.constants.MsgConstants;

public class PreferencePageWm extends BasePreferencePage 
{
	PreferenceInitializer m_pInit = null;
	
	public PreferencePageWm() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(MsgConstants.preferencesPageTitle);
	}
	
	@Override
	public boolean performOk()
	{
		boolean ret = super.performOk();

		try 
		{
			m_pInit.savePreferences();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() 
	{
		addField(new DirectoryFieldEditor(PreferenceConstants.cabWizardPath, 
				"&Cab wizard path:", getFieldEditorParent()));
				
		addField(new FileFieldEditor(PreferenceConstants.vcBuildPath, 
				"&VS build tool path:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
		m_pInit = PreferenceInitializer.getInstance();
	}	
}