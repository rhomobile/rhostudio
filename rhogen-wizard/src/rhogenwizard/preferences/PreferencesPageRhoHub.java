package rhogenwizard.preferences;


import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.sdk.task.rhohub.TokenTask;

public class PreferencesPageRhoHub extends BasePreferencePage 
{
    PreferenceInitializer m_pInit = null;
    
    public PreferencesPageRhoHub() 
    {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("");
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
                "&RhoHub API Endpoint (advanced):", getFieldEditorParent()));
                
        addField(new StringFieldEditor(IRhoHubSetting.rhoHubToken, 
                "&API Token:", getFieldEditorParent()));
        
        addField(new StringFieldEditor(IRhoHubSetting.rhoHubProxy, 
                "&HTTP proxy:", getFieldEditorParent()));
    }

    public void init(IWorkbench workbench) 
    {
        m_pInit = PreferenceInitializer.getInstance();
    }   
}