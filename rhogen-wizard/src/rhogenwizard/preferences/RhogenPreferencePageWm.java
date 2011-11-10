package rhogenwizard.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.buildfile.YmlFile;

public class RhogenPreferencePageWm extends BasePreferencePage 
{
	PreferenceInitializer m_pInit = null;
	
	public RhogenPreferencePageWm() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Rhodes rhobuild.yml preferences");
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
		checkRhodesSdk();
		
		addField(new RhogenDirectoryFieldEditor(PreferenceConstants.CAB_WIZARD_PATH, 
				"&Cab wizard path:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
		m_pInit = PreferenceInitializer.getInstance();
	}	
}