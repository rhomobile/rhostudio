package rhogenwizard.wizards.rhohub;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class LinkProjectPage extends WizardPage 
{
    private IProject m_project = null;

    Table   m_remoteProjectsList = null;
    Button m_newAppCheckBox     = null;
        
    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public LinkProjectPage(IProject project) 
    {
        super("wizardPage");
        setTitle("Link application with rhohub project wizard");
        setDescription("Link application with rhohub project wizard");
        
        m_project = project;
    }

    public void createAppSettingBarControls(Composite composite)
    {   
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 9;
        
        composite.setLayout(layout);
        
        GridData textAligment = new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
        
        GridData checkBoxAligment = new GridData();
        checkBoxAligment.horizontalAlignment = GridData.FILL;
        checkBoxAligment.horizontalSpan = 3;
        
        // 1 row
        m_newAppCheckBox = new Button(composite, SWT.CHECK);
        m_newAppCheckBox.setText("Use RhoElements");
        m_newAppCheckBox.setSelection(false);
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
        m_remoteProjectsList = new Table (composite, SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        m_remoteProjectsList.setItemCount (100);
        m_remoteProjectsList.setLayoutData(textAligment);
        m_remoteProjectsList.setEnabled(true);
        m_remoteProjectsList.addListener (SWT.SetData, new Listener () 
        {
            public void handleEvent (Event event) 
            {
                //TODO - temp sol.
                TableItem item = (TableItem) event.item;
                int index = m_remoteProjectsList.indexOf (item);
                item.setText ("Item " + index);
                System.out.println (item.getText ());
            }
        });  
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) 
    {   
        Composite container = new Composite(parent, SWT.NULL);
        
        createAppSettingBarControls(container);
        
        initialize();
        setControl(container);
    }
    
    private void initialize() 
    {       
        setDescription("");
        
        m_newAppCheckBox.setSelection(true);
    }

    /**
     * Ensures that both text fields are set.
     */
    private void dialogChanged()
    {
        m_remoteProjectsList.setEnabled(!m_newAppCheckBox.getSelection());
        
        updateStatus("Press finish for creation of project");
        updateStatus(null);
    }

    private void updateStatus(String message)
    {
        setErrorMessage(message);
        setPageComplete(message == null);
    }
}