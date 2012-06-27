package rhogenwizard.wizards.rhohub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import rhogenwizard.DialogUtils;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.IRhoHubSettingSetter;
import rhogenwizard.rhohub.RemotePlatformDesc;
import rhogenwizard.rhohub.RemotePlatformList;
import rhogenwizard.rhohub.RhoHub;
import rhogenwizard.rhohub.RhoHubBundleSetting;

class RemotePlatformAdapter implements Callable<RemotePlatformList>
{    
    IProject m_project = null;
    
    RemotePlatformAdapter(IProject project)
    {
        m_project = project;
    }
    
    @Override
    public RemotePlatformList call() throws Exception
    {
        IRhoHubSetting store = RhoHubBundleSetting.createGetter(m_project);

        if (store == null)
            return null;        
        
        return RhoHub.getInstance(store).getPlatformList();
    }
}

public class BuildSettingPage extends WizardPage 
{
    private class PlatfromInfoHolder
    {
        public PlatfromInfoHolder(String platformVersion, RemotePlatformDesc pl)
        {
            plVersion      = platformVersion;
            remotePlatform = pl;
        }
        
        public String             plVersion      = null;
        public RemotePlatformDesc remotePlatform = null;
    }

    private static int waitTimeOutRhoHubServer = 5;
    
    private IProject m_project = null;
    private Map<String, List<PlatfromInfoHolder>> m_platformsInfo = null;
    
    private Combo m_comboPlatforms         = null;
    private Combo m_comboPlatformVersions  = null;
    private Text  m_textAppBranch          = null;
    private Text  m_textRhodesBranch       = null;
    
    private RemotePlatformList         m_remotePlatforms = null;
    private Future<RemotePlatformList> m_getPlatfomListFuture = null;
    
    void enableControls(boolean enable)
    {
        m_comboPlatforms.setEnabled(enable);
        m_comboPlatformVersions.setEnabled(enable);
        m_textAppBranch.setEnabled(enable);
        m_textRhodesBranch.setEnabled(enable);
    }
    
    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public BuildSettingPage(IProject project) 
    {
        super("wizardPage");
        setTitle("RhoHub build application wizard");
        setDescription("RhoHub build application wizard");
        
        m_project = project;
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        
        try
        {
            updateStatus("Please wait until the server request.");
            
            m_remotePlatforms = m_getPlatfomListFuture.get(waitTimeOutRhoHubServer, TimeUnit.MINUTES);            
            initializePlatformsCombo();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            DialogUtils.error("Connect error", "Not response from Rhohub server. Please try run build sometime later.");
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            DialogUtils.error("Connect error", "Not response from Rhohub server. Please try run build sometime later.");
            e.printStackTrace();
        }
 
        enableControls(true);
    }

    public void createAppSettingBarControls(Composite composite)
    {   
        GridLayout layout = new GridLayout(4, true);
        layout.verticalSpacing = 9;
        
        composite.setLayout(layout);
        
        GridData textAligment = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);        
                   
        // 1 row
        Label label = new Label(composite, SWT.NULL);
        label.setText("&App branch:");

        label = new Label(composite, SWT.NULL);
        label.setText("&Rhodes branch:");

        label = new Label(composite, SWT.NULL);
        label.setText("&Targer device:");

        label = new Label(composite, SWT.NULL);
        label.setText("&Platform version:");

        // 2 row
        m_textAppBranch = new Text(composite, SWT.BORDER | SWT.SINGLE);
        m_textAppBranch.setLayoutData(textAligment);
        m_textAppBranch.addModifyListener(new ModifyListener() 
        {
            public void modifyText(ModifyEvent e) 
            {
                dialogChanged();
            }
        });
        
        m_textRhodesBranch = new Text(composite, SWT.BORDER | SWT.SINGLE);
        m_textRhodesBranch.setLayoutData(textAligment);
        m_textRhodesBranch.addModifyListener(new ModifyListener() 
        {
            public void modifyText(ModifyEvent e) 
            {
                dialogChanged();
            }
        });
        
        m_comboPlatforms = new Combo(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
        m_comboPlatforms.setLayoutData(textAligment);
        m_comboPlatforms.addModifyListener(new ModifyListener() 
        {
            public void modifyText(ModifyEvent e) 
            {
                List<PlatfromInfoHolder> versions = m_platformsInfo.get(m_comboPlatforms.getText());
                
                m_comboPlatformVersions.removeAll();
                
                for (PlatfromInfoHolder ver : versions)
                {
                    m_comboPlatformVersions.add(ver.plVersion);
                    m_comboPlatformVersions.setData(ver.plVersion, ver.remotePlatform);
                }
                m_comboPlatformVersions.select(0);
            }
        });
        
        m_comboPlatformVersions = new Combo(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
        m_comboPlatformVersions.setLayoutData(textAligment);
        m_comboPlatformVersions.addModifyListener(new ModifyListener() 
        {
            public void modifyText(ModifyEvent e) 
            {
                dialogChanged();
            }
        });
        
        enableControls(false);
    }

    public void createControl(Composite parent) 
    {   
        Composite container = new Composite(parent, SWT.NULL);
        
        createAppSettingBarControls(container);
        
        initialize();
        setControl(container);
    }
    
    private void initializePlatformsCombo()
    {
        if (m_remotePlatforms == null || m_remotePlatforms.size() == 0)
        {
            DialogUtils.error("Connect error", "Rhohub server is not avaialible. Please try run build sometime later.");
            
            m_comboPlatforms.setEnabled(false);
            m_comboPlatformVersions.setEnabled(false);            
            this.getShell().close();
            
            return;
        }
        else
        {
            m_platformsInfo = new HashMap<String, List<PlatfromInfoHolder>>();  
            
            for (RemotePlatformDesc pl : m_remotePlatforms)
            {
                List<PlatfromInfoHolder> plVers = m_platformsInfo.get(pl.getPlatformName());
                
                PlatfromInfoHolder item  = new PlatfromInfoHolder(pl.getPlatformVersion(), pl);
                
                if (plVers == null)
                {
                    plVers = new ArrayList<PlatfromInfoHolder>();
                    plVers.add(item);

                    m_platformsInfo.put(pl.getPlatformName(), plVers);
                }
                else
                {
                    plVers.add(item);
                }
            }

            Set<String> plList = m_platformsInfo.keySet();
            
            for (String plName : plList)
            {
                m_comboPlatforms.add(plName);    
            }
            m_comboPlatforms.select(0);
        }
    }
    
    private void initialize() 
    {       
        setDescription("");
        
        m_comboPlatforms.setEnabled(true);
        m_comboPlatformVersions.setEnabled(true);
        
        // run async request to rhohub server
        ExecutorService executor = Executors.newSingleThreadExecutor();
        m_getPlatfomListFuture = executor.submit(new RemotePlatformAdapter(m_project));

        m_textRhodesBranch.setText("3.3.2");
        m_textAppBranch.setText("master");
    }

    /**
     * Ensures that both text fields are set.
     */
    private void dialogChanged()
    {
        if (m_comboPlatforms.getText().isEmpty())
        {
            updateStatus("RhoHub platform should be selected");
            return;
        }
        
        try
        {
            IRhoHubSettingSetter store = RhoHubBundleSetting.createSetter(m_project);
            
            store.setRhodesBranch(m_textRhodesBranch.getText());

            // if not selected item from list in store stored empty string
            RemotePlatformDesc desc = (RemotePlatformDesc) m_comboPlatformVersions.getData(m_comboPlatformVersions.getText());
            
            if (desc == null)
                return;
            
            store.setSelectedPlatform(desc.getInternalName());
        }
        catch (BackingStoreException e)
        {
            e.printStackTrace();
        }
        
        updateStatus(null);
        setMessage("Press finish for start remote project build");
    }

    private void updateStatus(String message)
    {
        setErrorMessage(message);
        setPageComplete(message == null);
    }
}