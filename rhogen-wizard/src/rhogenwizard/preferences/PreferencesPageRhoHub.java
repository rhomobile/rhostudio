package rhogenwizard.preferences;


import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.constants.MsgConstants;
import rhogenwizard.rhohub.IRhoHubSetting;

public class PreferencesPageRhoHub extends BasePreferencePage 
{
    PreferenceInitializer m_pInit = null;
    
    public PreferencesPageRhoHub() 
    {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(MsgConstants.preferencesRhoHubTitle);
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

    public void createFieldEditors() 
    {
        checkRhodesSdk();
        
        addField(new StringFieldEditor(IRhoHubSetting.rhoHubUrl, 
                "&RhoHub server url:", getFieldEditorParent()));
                
        addField(new StringFieldEditor(IRhoHubSetting.rhoHubToken, 
                "&User token:", getFieldEditorParent()));

        addField(new StringFieldEditor(IRhoHubSetting.rhoHubSelectedRhodesVesion, 
            "&Default rhodes version:", getFieldEditorParent()));
    }

    public void init(IWorkbench workbench) 
    {
        m_pInit = PreferenceInitializer.getInstance();
    }   
}