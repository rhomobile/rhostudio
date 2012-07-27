package rhogenwizard.wizards.rhohub;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.json.JSONException;
import org.osgi.service.prefs.BackingStoreException;

import rhogenwizard.DialogUtils;
import rhogenwizard.HttpDownload;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.IRhoHubSettingSetter;
import rhogenwizard.rhohub.JSONList;
import rhogenwizard.rhohub.RemoteAppBuildDesc;
import rhogenwizard.rhohub.RemotePlatformDesc;
import rhogenwizard.rhohub.RhoHub;
import rhogenwizard.rhohub.RhoHubBundleSetting;

class RemotePlatformAdapter implements Callable<JSONList<RemotePlatformDesc>>
{    
    IProject m_project = null;
    
    RemotePlatformAdapter(IProject project)
    {
        m_project = project;
    }
    
    @Override
    public JSONList<RemotePlatformDesc> call() throws Exception
    {
        IRhoHubSetting store = RhoHubBundleSetting.createGetter(m_project);

        if (store == null)
            return null;        
        
        return RhoHub.getInstance(store).getPlatformList();
    }
}

class RemoteAppBuildsAdapter implements Callable<JSONList<RemoteAppBuildDesc>>
{    
    IProject m_project = null;
    
    RemoteAppBuildsAdapter(IProject project)
    {
        m_project = project;
    }
    
    @Override
    public JSONList<RemoteAppBuildDesc> call() throws Exception
    {
        IRhoHubSetting store = RhoHubBundleSetting.createGetter(m_project);

        if (store == null)
            return null;        
        
        return RhoHub.getInstance(store).getBuildsList(m_project);
    }
}

class BuildDownload implements Runnable
{
    final URL    m_buildUrl;
    final String m_dstDir;
    
    String    m_fileName = null;
    
    BuildDownload(final URL buildUrl, final String dstDir)
    {
        m_buildUrl = buildUrl;
        m_dstDir   = dstDir;
        
        String dwlLink = buildUrl.toString();
        
        int nameStartIdx = dwlLink.lastIndexOf("/");
        
        if (nameStartIdx != -1)
        {
            m_fileName = dwlLink.substring(nameStartIdx);    
        }
    }
    
    @Override
    public void run()
    {
        try
        {
            File resultFile = new File(m_dstDir + File.separator + m_fileName);
            
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            HttpDownload hd = new HttpDownload(m_buildUrl, os);
            hd.join(0);
            
            FileOutputStream foStream = new FileOutputStream(resultFile);
            os.writeTo(foStream);

            foStream.close();
            os.close();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
    private Table m_remoteBuildsList       = null;
    
    private JSONList<RemotePlatformDesc>           m_remotePlatforms = null;
    private JSONList<RemoteAppBuildDesc>           m_remoteProjectBuilds = null;
    private Future<JSONList<RemotePlatformDesc> >  m_getPlatfomListFuture = null;
    private Future<JSONList<RemoteAppBuildDesc>>   m_getProjectBuildsFuture = null;
    
    void enableControls(boolean enable)
    {
        m_comboPlatforms.setEnabled(enable);
        m_comboPlatformVersions.setEnabled(enable);
        m_textAppBranch.setEnabled(enable);
        m_textRhodesBranch.setEnabled(enable);
        m_remoteBuildsList.setEnabled(enable);
    }
    
    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public BuildSettingPage(IProject project) 
    {
        super("wizardPage");
        setTitle("RhoHub Application Build Wizard");
        setDescription("RhoHub Application Build Wizard");
        
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
            
            m_remoteProjectBuilds = m_getProjectBuildsFuture.get(waitTimeOutRhoHubServer, TimeUnit.MINUTES);
            initializeProjectBuildsTable();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return;
        }
        catch (ExecutionException e)
        {
            DialogUtils.error("Connect error", "Not response from Rhohub server. Please try run build sometime later.");
            e.printStackTrace();
            return;
        }
        catch (TimeoutException e)
        {
            DialogUtils.error("Connect error", "Not response from Rhohub server. Please try run build sometime later.");
            e.printStackTrace();
            return;
        }
        catch (JSONException e)
        {
            DialogUtils.error("Connect error", "The information from Rhohub server was corrupted. Please try run build sometime later.");
            e.printStackTrace();
            return;
        }
        catch (MalformedURLException e)
        {
            DialogUtils.error("Connect error", "Download link is broken. Please try it sometime later.");
            e.printStackTrace();
            return;
        }
 
        enableControls(true);
    }

    public void createAppSettingBarControls(Composite composite)
    {   
        GridLayout layout = new GridLayout(4, true);
        layout.verticalSpacing = 9;
        
        composite.setLayout(layout);
        
        GridData textAligment = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);        
                   
        GridData tableAligment = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableAligment.heightHint = 200;
        tableAligment.horizontalSpan = 4;
        
        // 1 row
        Label label = new Label(composite, SWT.NULL);
        label.setText("&App Git Revision:");

        label = new Label(composite, SWT.NULL);
        label.setText("&Rhodes Git Revision:");

        label = new Label(composite, SWT.NULL);
        label.setText("&Target Device:");

        label = new Label(composite, SWT.NULL);
        label.setText("&Platform Version:");

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
        
        // 3 row
        m_remoteBuildsList = new Table (composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        m_remoteBuildsList.setLayoutData(tableAligment);
        m_remoteBuildsList.setEnabled(true);
        m_remoteBuildsList.setHeaderVisible(true);
        m_remoteBuildsList.setLinesVisible(true);
        
        TableColumn colUrl  = new TableColumn(m_remoteBuildsList, SWT.LEFT);        
        colUrl.setText("Download link");
        colUrl.setWidth(150);

        TableColumn colStatus  = new TableColumn(m_remoteBuildsList, SWT.RIGHT);        
        colStatus.setText("Status");
        colStatus.setWidth(200);
        
        enableControls(false);
    }
    
    private void addTableLine(TableItem newItem, RemoteAppBuildDesc projectBuild) throws JSONException, MalformedURLException
    {
        TableEditor colTwoEditor = new TableEditor(m_remoteBuildsList);
        colTwoEditor.grabHorizontal = true;
        Button dwlButton = new Button(m_remoteBuildsList, SWT.PUSH | SWT.VIRTUAL);
        dwlButton.setText("Download");
        dwlButton.setData(projectBuild.getBuildResultUrl());
        dwlButton.addSelectionListener(new SelectionListener()
        {
            private String getDirectory()
            {
                DirectoryDialog dlg = new DirectoryDialog(Display.getCurrent().getActiveShell());

                dlg.setFilterPath("C:");
                dlg.setText("Select destination directory");
                dlg.setMessage("Select a directory");
                
                return dlg.open();
            }
            
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Button parentBtn = (Button)e.widget;
                
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new BuildDownload((URL) parentBtn.getData(), getDirectory()));
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });
        colTwoEditor.setEditor(dwlButton, newItem, 0);

        TableEditor colThreeEditor = new TableEditor(m_remoteBuildsList);
        colThreeEditor.grabHorizontal = true;        
        Label prjStatusLabel = new Label(m_remoteBuildsList, SWT.RIGHT);
        prjStatusLabel.setText(projectBuild.getBuildStatus().toString() + " ");
        colThreeEditor.setEditor(prjStatusLabel, newItem, 1);
    }
    
    public void createControl(Composite parent) 
    {   
        Composite container = new Composite(parent, SWT.NULL);
        
        createAppSettingBarControls(container);
        
        initialize();
        setControl(container);
    }
    
    private void initializeProjectBuildsTable() throws JSONException, MalformedURLException
    {
        if (m_remoteProjectBuilds == null)
        {
            DialogUtils.error("Connect error", "Rhohub server is not avaialible. Please try run build sometime later.");
            
            m_comboPlatforms.setEnabled(false);
            m_comboPlatformVersions.setEnabled(false);            
            this.getShell().close();
            
            return;
        }
        else
        {
            for (RemoteAppBuildDesc prjDesc : m_remoteProjectBuilds)
            {
                TableItem newItem = new TableItem(m_remoteBuildsList, SWT.NONE);
                addTableLine(newItem, prjDesc);
            }
        }
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
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        m_getPlatfomListFuture   = executor.submit(new RemotePlatformAdapter(m_project));
        m_getProjectBuildsFuture = executor.submit(new RemoteAppBuildsAdapter(m_project));
        
        IRhoHubSetting setting = RhoHubBundleSetting.createGetter(m_project);
        
        m_textRhodesBranch.setText(setting.getRhodesBranch());
        m_textAppBranch.setText(setting.getAppBranch()); 
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