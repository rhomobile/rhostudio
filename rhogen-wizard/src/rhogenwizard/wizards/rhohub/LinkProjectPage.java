package rhogenwizard.wizards.rhohub;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.json.JSONException;

import rhogenwizard.DialogUtils;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.JSONList;
import rhogenwizard.rhohub.RemoteProjectDesc;
import rhogenwizard.rhohub.RhoHub;

public class LinkProjectPage extends WizardPage 
{
    private static int nameColIdx = 0;
    private static int urlColIdx = 1;
    
    private IRhoHubSetting m_setting = null;
    private boolean        m_isNewProject = true;
    
    private Table  m_remoteProjectsList = null;
    private Button m_newAppCheckBox     = null;
        
    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public LinkProjectPage(IProject project, IRhoHubSetting setting) 
    {
        super("wizardPage");
        setTitle("Link application with rhohub project wizard");
        setDescription("Link application with rhohub project wizard");
        
        m_setting = setting;
    }

    public void createAppSettingBarControls(Composite composite)
    {   
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 9;
        
        composite.setLayout(layout);
                
        GridData tableAligment = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableAligment.heightHint = 200;
        
        GridData checkBoxAligment = new GridData();
        checkBoxAligment.horizontalAlignment = GridData.FILL;
        checkBoxAligment.horizontalSpan = 3;
        
        // 1 row
        m_newAppCheckBox = new Button(composite, SWT.CHECK);
        m_newAppCheckBox.setText("Create new project on RhoHub server");
        m_newAppCheckBox.setSelection(m_isNewProject);
        m_newAppCheckBox.setLayoutData(checkBoxAligment);
        m_newAppCheckBox.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e)
            {
                dialogChanged();
            }
        });
        
        // 2 row
        Label label = new Label(composite, SWT.NULL);
        label.setText("Select project on RhoHub for linking:");

        // 3 row
        m_remoteProjectsList = new Table (composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        m_remoteProjectsList.setLayoutData(tableAligment);
        m_remoteProjectsList.setEnabled(true);
        m_remoteProjectsList.setHeaderVisible(true);
        m_remoteProjectsList.setLinesVisible(true);
        
        TableColumn colName = new TableColumn(m_remoteProjectsList, SWT.LEFT);
        colName.setText("Project name");
        colName.setWidth(350);
        
        TableColumn colUrl  = new TableColumn(m_remoteProjectsList, SWT.LEFT);        
        colUrl.setText("Project url");
        colUrl.setWidth(400);
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) 
    {   
        Composite container = new Composite(parent, SWT.NULL);
        
        createAppSettingBarControls(container);
        
        initialize();
        dialogChanged();
        
        setControl(container);
    }
    
    private void initialize() 
    {       
        setDescription("");

        try
        {
            JSONList<RemoteProjectDesc> remoteProjects = RhoHub.getInstance(m_setting).getProjectsList();

            if (remoteProjects.size() == 0)
            {
                DialogUtils.error("Connect error", "Not response from Rhohub server. Please try run build sometime later.");
                this.getShell().close();
                return;
            }
            else
            {
                for (RemoteProjectDesc project : remoteProjects)
                {
                    TableItem item = new TableItem(m_remoteProjectsList, SWT.NONE);
                    
                    item.setText(nameColIdx, project.getName());
                    item.setText(urlColIdx, project.getGitLink());
                }
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Ensures that both text fields are set.
     */
    private void dialogChanged()
    {
        m_remoteProjectsList.setEnabled(!m_newAppCheckBox.getSelection());
        
        m_isNewProject = m_newAppCheckBox.getSelection();
        
        updateStatus("Press finish for link remote project with local sources");
        updateStatus(null);
    }

    private void updateStatus(String message)
    {
        setErrorMessage(message);
        setPageComplete(message == null);
    }
        
    public boolean isNewProject()
    {
        return m_isNewProject;
    }
    
    public String getSelectedProjectUrl()
    {
        return m_remoteProjectsList.getSelection()[urlColIdx].toString();
    }
}