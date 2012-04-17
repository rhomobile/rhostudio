package rhogenwizard.preferences;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.constants.MsgConstants;


public class RhogenPreferencePageBb extends BasePreferencePage implements IItemAddedNotifier
{
	private static final String newVersionLabel = "New version";
	
	private PreferenceInitializer       m_pInit = null;
	private RhogenComboFieldEditor 	    m_selectCombo = null;
	private RhogenDirectoryFieldEditor 	m_jdkDir = null;
	private RhogenDirectoryFieldEditor 	m_mdsDir = null;
	private StringFieldEditor 		    m_simPort = null;
	private StringFieldEditor           m_bbVer = null;
	private int                         m_selComboItem = 0;
	
	public RhogenPreferencePageBb() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(MsgConstants.preferencesPageTitle);
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
			String bbVersionName = m_selectCombo.getCombo().getItem(m_selComboItem);
			String bbJdkPath     = m_jdkDir.getStringValue();
			String bbMdsPath     = m_mdsDir.getStringValue();
			String bbSimPort     = m_simPort.getStringValue();
			
			getPreferenceStore().setValue(PreferenceConstants.bbVersionName, bbVersionName);
			getPreferenceStore().setValue(PreferenceConstants.bbJdkPath, bbJdkPath);
			getPreferenceStore().setValue(PreferenceConstants.bbMdsPath, bbMdsPath);
			getPreferenceStore().setValue(PreferenceConstants.bbSim, bbSimPort);

			m_pInit.savePreferences();
			
			if (m_selectCombo.getCombo().getText().equals(newVersionLabel))
			{
				int insIndex = m_selectCombo.getCombo().getItemCount() - 1;
				insIndex = insIndex > 0 ? insIndex : 0;
				m_selectCombo.getCombo().add(bbVersionName, insIndex);
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
		
		if (bbVers == null || bbVers.size() == 0)
		{
			String[][] fake = {{"", ""}};
			return fake;
		}
		
		String[][] comboItems = new String[bbVers.size()/* + 1*/][];
		
		for (int i=0; i<bbVers.size(); ++i)
		{
			String ver = bbVers.get(i);
			String[] newItem = {ver, ver};
			comboItems[i] = newItem;
		}
		
		return comboItems;
	}

	public void createFieldEditors() 
	{
		checkRhodesSdk();
		
		m_selectCombo = new RhogenComboFieldEditor(PreferenceConstants.bbVersion, 
				"Blackberry version:", prepareComboItems(), getFieldEditorParent(), this);
		
		m_selectCombo.setSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try 
				{
					// for save before change blackberry sdk
					performOk();
					
					m_selComboItem = m_selectCombo.getCombo().getSelectionIndex();
					
					String bbVer = m_selectCombo.getCombo().getText();
					
					SdkYmlFile ymlFile = m_pInit.getYmlFile();

					if (ymlFile != null)
					{
						String bbJdkPath = ymlFile.getBbJdkPath(bbVer) != null ? ymlFile.getBbJdkPath(bbVer) : "";
						String bbMdsPath = ymlFile.getBbMdsPath(bbVer) != null ? ymlFile.getBbMdsPath(bbVer) : "";
						String bbSimPort = ymlFile.getBbSimPort(bbVer) != null ? ymlFile.getBbSimPort(bbVer) : "";

						m_mdsDir.setStringValue(bbMdsPath);
						m_jdkDir.setStringValue(bbJdkPath);
						m_simPort.setStringValue(bbSimPort);
					}
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		
		addField(m_selectCombo);
		
		m_jdkDir = new RhogenDirectoryFieldEditor(PreferenceConstants.bbJdkPath, 
				"&Blackbery JDE path:", getFieldEditorParent());
		addField(m_jdkDir);
		
		m_mdsDir = new RhogenDirectoryFieldEditor(PreferenceConstants.bbMdsPath, 
				"&Blackbery MDS path:", getFieldEditorParent());
		addField(m_mdsDir);
		
		m_simPort = new StringFieldEditor(PreferenceConstants.bbSim, 
				"Simulator device:", getFieldEditorParent());
		addField(m_simPort);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
		m_pInit = PreferenceInitializer.getInstance();
	}

	@Override
	public void addNewValue(String value) 
	{
	}	
}