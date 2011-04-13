package rhogenwizard.preferences;

import java.util.List;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;

public class RhogenPreferencePageBb extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	private RhogenComboFieldEditor 	    m_selectCombo = null;
	private RhogenDirectoryFieldEditor 	m_jdkDir = null;
	private RhogenDirectoryFieldEditor 	m_mdsDir = null;
	private StringFieldEditor 		    m_simPort = null;
	private StringFieldEditor           m_bbVer = null;
	private PreferenceInitializer    	m_pInit = new PreferenceInitializer();
	
	public RhogenPreferencePageBb() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Rhodes rhobuild.yml preferences");
	}

	@Override
	public boolean okToLeave() 
	{
		return  true;
	}

	@Override
	public boolean performOk()
	{
		boolean ret = super.performOk();

		try 
		{
			String bbVersionName = m_bbVer.getStringValue();
			String bbJdkPath     = m_jdkDir.getStringValue();
			String bbMdsPath     = m_mdsDir.getStringValue();
			String bbSim         = m_simPort.getStringValue();
			
			SdkYmlFile ymlFile = SdkYmlAdapter.getRhobuildFile();
			
			if (ymlFile != null)
			{
				ymlFile.setBbJdkPath(bbVersionName, bbJdkPath);
				ymlFile.setBbMdsPath(bbVersionName, bbMdsPath);
				ymlFile.setBbSimPort(bbVersionName, new Integer(bbSim));
				
				ymlFile.save();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return ret;
	}
	
	private String[][] prepareComboItems()
	{
		List<String> bbVers = m_pInit.getBbVersions();
		
		String[][] comboItems = new String[bbVers.size() + 1][];
		
		for (int i=0; i<bbVers.size(); ++i)
		{
			String ver = bbVers.get(i);
			String[] newItem = {ver, ver};
			comboItems[i] = newItem;
		}
		
		String[] newVerItem = {"New version", "New version"};
		comboItems[bbVers.size()] = newVerItem;

		return comboItems;
	}

	public void createFieldEditors() 
	{
		m_selectCombo = new RhogenComboFieldEditor(PreferenceConstants.BB_VERSION, 
				"Blackberry version:", prepareComboItems(), getFieldEditorParent());
		
		m_selectCombo.setSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try 
				{
					String bbVer = m_selectCombo.getCombo().getText();
					
					SdkYmlFile ymlFile = SdkYmlAdapter.getRhobuildFile();

					if (ymlFile != null)
					{
						String bbJdkPath = ymlFile.getBbJdkPath(bbVer) != null ? ymlFile.getBbJdkPath(bbVer) : "";
						String bbMdsPath = ymlFile.getBbMdsPath(bbVer) != null ? ymlFile.getBbMdsPath(bbVer) : "";
						String bbSimPort = ymlFile.getBbSimPort(bbVer) != null ? ymlFile.getBbSimPort(bbVer) : "";

						m_mdsDir.setStringValue(bbMdsPath);
						m_jdkDir.setStringValue(bbJdkPath);
						m_simPort.setStringValue(bbSimPort);
						m_bbVer.setStringValue(bbVer);
					}
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		
		addField(m_selectCombo);
		
		m_bbVer = new StringFieldEditor(PreferenceConstants.BB_VERSION_NAME, 
				"Version name:", getFieldEditorParent());
		addField(m_bbVer);
		
		m_jdkDir = new RhogenDirectoryFieldEditor(PreferenceConstants.BB_JDK_PATH, 
				"&Blackbery JDE path:", getFieldEditorParent());
		addField(m_jdkDir);
		
		m_mdsDir = new RhogenDirectoryFieldEditor(PreferenceConstants.BB_MDS_PATH, 
				"&Blackbery MDS path:", getFieldEditorParent());
		addField(m_mdsDir);
		
		m_simPort =  new StringFieldEditor(PreferenceConstants.BB_SIM, 
				"Simulator device:", getFieldEditorParent());
		addField(m_simPort);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
		m_pInit.initializeDefaultPreferences();
	}	
}