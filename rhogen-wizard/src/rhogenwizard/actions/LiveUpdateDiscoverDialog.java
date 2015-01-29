package rhogenwizard.actions;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.progress.UIJob;

import rhogenwizard.sdk.task.liveupdate.DiscoverTask;
import rhogenwizard.sdk.task.liveupdate.PrintSubnetsTask;

class TableItemUpdateUIJob extends UIJob
{
	private static String[] discoverStatus = {"Found", "Not Found", "Empty", "Search..."};

	private int       m_showIndex = 0;
	private TableItem m_item = null; 
	
	public TableItemUpdateUIJob(TableItem item, int showIndex)
	{
		super("Update UI");
		
		m_showIndex = showIndex;
		m_item      = item;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) 
	{
		m_item.setText(1, discoverStatus[m_showIndex]);
		return Status.OK_STATUS;
	}
}

class SearchProgressMonitor implements Runnable
{
	private TableItem m_item = null; 
	private IProject  m_project = null;
	private String    m_subnetMask = null;
	
	private static int foundId    = 0;
	private static int notFoundId = 1;
	private static int emptyId    = 2;
	private static int searchId   = 3;

	public SearchProgressMonitor(IProject project, String subnetMask, TableItem item)
	{	
		m_item       = item;
		m_project    = project;
		m_subnetMask = subnetMask;
	}

	@Override
	public void run()
	{
		try 
		{	
			new TableItemUpdateUIJob(m_item, searchId).schedule();
			
			DiscoverTask task = new DiscoverTask(m_project.getLocation().toOSString(), m_subnetMask);		
			Job taskJob = task.makeJob("Discover devices in " + m_subnetMask + " subnet");
			
			taskJob.schedule();
			taskJob.join();
			
			if (task.isDeviceFound()) {
				new TableItemUpdateUIJob(m_item, foundId).schedule();
			}
			else {
				new TableItemUpdateUIJob(m_item, notFoundId).schedule();
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
}

class DiscoverSubnet implements Runnable
{
	private List<String> m_subnets = null;
	private String       m_pathToProject = null;

	public DiscoverSubnet(IProject project)
	{
		m_pathToProject = project.getLocation().toOSString();
	}
	
	@Override
	public void run() 
	{
		PrintSubnetsTask task = new PrintSubnetsTask(m_pathToProject);
		
		task.run();
		
		//if (task.isOk()) {
			m_subnets = task.getSubnets();
		//}
		
		try { Thread.sleep(2000); } catch (InterruptedException e) {}//debug
	}	
	
	public List<String> getSubnets()
	{
		return m_subnets;
	}	
}

public class LiveUpdateDiscoverDialog extends TitleAreaDialog 
{
    private static String[] discoverStatus = {"Found", "Not Found", "Empty", "Search..."};

    private static int foundId    = 0;
    private static int notFoundId = 1;
    private static int emptyId    = 2;
    private static int searchId   = 3;
    
    private IProject m_project = null;
        
	public LiveUpdateDiscoverDialog(IProject project, Shell parentShell) 
	{
		super(parentShell);
		
		m_project = project;
	}

	@Override
	public void create() 
	{
		super.create();
		setTitle("Live update discover");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{	
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout(1, true));

		new Label(container, SWT.NONE).setText("Subnets:");
		
		Composite tblContainer = new Composite(container, SWT.NONE);
		tblContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tblContainer.setLayout(new FillLayout());
		
		final Table m_subnetTable = new Table(tblContainer, SWT.SINGLE | SWT.FULL_SELECTION);
		m_subnetTable.setHeaderVisible(true);
		m_subnetTable.setLinesVisible(true);
		TableColumn columnSubnetIp = new TableColumn(m_subnetTable, SWT.NONE);
		columnSubnetIp.setText("Subnet");
		columnSubnetIp.setWidth(100);
		
		final TableColumn column2 = new TableColumn(m_subnetTable, SWT.CENTER);
		column2.setText("Status");
		column2.setWidth(300);

		fillTable(m_subnetTable);
		
		m_subnetTable.addListener(SWT.MouseDoubleClick, new Listener()
		{			
			@Override
			public void handleEvent(Event event) 
			{
				dblClickHandler((TableItem)m_subnetTable.getSelection()[0]);
			}
		});
		 
        return area;
    }

	private void dblClickHandler(TableItem item) 
	{
		new Thread(new SearchProgressMonitor(m_project, item.getText(0),  item)).start();
	}

	private void fillTable(Table table)
	{
		DiscoverSubnet discoverJob = new DiscoverSubnet(m_project);
		BusyIndicator.showWhile(getShell().getDisplay(), discoverJob);
				
		for (String itemName : discoverJob.getSubnets()) {
			 TableItem item = new TableItem(table, SWT.NONE);
			 item.setText(itemName);
			 item.setText(1, discoverStatus[emptyId]);
		}
	}
	
	@Override
	protected boolean isResizable() 
	{
		return true;
	}

	@Override
	protected void okPressed() 
    {
        super.okPressed();
    }
}