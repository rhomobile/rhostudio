package rhogenwizard.editors;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.UIJob;

import rhogenwizard.sdk.task.JobNotificationMonitor;
import rhogenwizard.sdk.task.liveupdate.DiscoverTask;
import rhogenwizard.sdk.task.liveupdate.LUDevice;
import rhogenwizard.sdk.task.liveupdate.LiveUpdateTask;
import rhogenwizard.sdk.task.liveupdate.PrintSubnetsTask;
 
class LiveUpdateObserver extends Observable 
{
	public void notifyUi(Object arg)
	{
		this.setChanged();
		this.notifyObservers(arg);
	}
}

class TableItemUpdateUIJob extends UIJob
{
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
		if (!m_item.isDisposed())
			m_item.setText(1, LiveUpdateEditor.discoverStatus[m_showIndex]);
		
		return Status.OK_STATUS;
	}
}

class DevicesTableFillUIJob extends UIJob
{
	private Table    m_table   = null; 
	private IProject m_project = null;
	
	public DevicesTableFillUIJob(IProject project, Table table)
	{
		super("Update UI");
		
		m_table   = table;
		m_project = project;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) 
	{
		if (m_table.isDisposed())
			return Status.OK_STATUS;
		
		m_table.removeAll();
		
		IPath path = m_project.getLocation();
		path = path.append(LUDevice.configFileName);
		
		try 
		{
			List<LUDevice> devices = LUDevice.load(path);
			
			if (devices != null && devices.size() != 0)
			{
				for (LUDevice itemDev : devices) {
					 TableItem item = new TableItem(m_table, SWT.NONE);
					 item.setText(0, itemDev.Name);
					 item.setText(1, itemDev.URI);
					 item.setText(2, itemDev.Application);
					 item.setText(3, itemDev.Platfrom);
				}
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		return Status.OK_STATUS;
	}
}

class SearchProgressMonitor implements Runnable
{
	private static final int updateUiSleep = 1000;
	
	private TableItem m_item = null; 
	private IProject  m_project = null;
	private String    m_subnetMask = null;

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
			new TableItemUpdateUIJob(m_item, LiveUpdateEditor.searchId).schedule();
			
			DiscoverTask task = new DiscoverTask(m_project.getLocation().toOSString(), m_subnetMask);		
			Job taskJob = task.makeJob("Discover devices in " + m_subnetMask + " subnet");
			taskJob.schedule();
			
			int searchIdx = 0;
			int maxSearchAnimSteps = 2;
			
			while(true)
			{
				if(searchIdx >= maxSearchAnimSteps)
					searchIdx = 0;
				else
					searchIdx++;
				
				new TableItemUpdateUIJob(m_item, LiveUpdateEditor.searchId + searchIdx).schedule();
				
				if (taskJob.getResult() != null)
					break;
				
				if (m_item.isDisposed())
				{
					taskJob.cancel();
					break;
				}
				
				Thread.sleep(updateUiSleep);
			}
			
			if (task.isOk()) {
				new TableItemUpdateUIJob(m_item, LiveUpdateEditor.foundId).schedule();
			}
			else {
				new TableItemUpdateUIJob(m_item, LiveUpdateEditor.notFoundId).schedule();
			}
			
			LiveUpdateEditor.eventHandler.notifyUi("update");
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
		
		if (task.isOk()) 
		{
			m_subnets = task.getSubnets();
		}
	}	
	
	public List<String> getSubnets()
	{
		return m_subnets;
	}	
}

class LUJobNotifications implements JobNotificationMonitor
{
	class OffButtonSelectionUiJob extends UIJob
	{
		final Button m_btn;
		
		public OffButtonSelectionUiJob(final Button btn, String name) 
		{
			super(name);
			
			m_btn = btn;
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) 
		{
			m_btn.setSelection(false);
			m_btn.setText(LiveUpdateEditor.switchLUButtonText[LiveUpdateEditor.liveUpdateEnableId]);
			return Status.OK_STATUS;
		}	
	}
	
	OffButtonSelectionUiJob m_uiJob;
	
	public LUJobNotifications(final Button btn)
	{
		m_uiJob = new OffButtonSelectionUiJob(btn, "update ui");
	}
	
	@Override
	public void onJobStop() 
	{
		m_uiJob.schedule();
	}

	@Override
	public void onJobStart() {
	}

	@Override
	public void onJobFinished() {
	}
}

public class LiveUpdateEditor extends EditorPart implements Observer
{
	private static QualifiedName isLiveUpdateEnableTag = new QualifiedName(null, "is-live-update-ebnable");
	
	public static String[] discoverStatus = {"Found", "Not Found", "Empty", "Search.", "Search..", "Search..."};
	public static String[] switchLUButtonText = {"Enable live update", "Disable live update"};
			
	public static LiveUpdateObserver eventHandler =  new LiveUpdateObserver();
	
    public static int liveUpdateEnableId  = 0;
	public static int liveUpdateDisableId = 1;
	
    public static int foundId    = 0;
    public static int notFoundId = 1;
    public static int emptyId    = 2;
    public static int searchId   = 3;
    
    private static GridData textAligment = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
        	
	private IProject m_project = null;
	private Table    m_devicesTable = null;
	
	@Override
	public void doSave(IProgressMonitor monitor) 
	{
	}

	@Override
	public void doSaveAs() 
	{
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException 
	{
		if (input instanceof FileEditorInput)
		{
			FileEditorInput fileInput = (FileEditorInput)input;
			m_project = (IProject)fileInput.getFile().getParent();
		}
		
		setSite(site);
		setInput(input);
		
		eventHandler.addObserver(this);
	}

	@Override
	public boolean isDirty() 
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() 
	{
		return false;
	}

	@Override
	public void setFocus() 
	{
	}

	@Override
	public void createPartControl(Composite parent) 
	{
		if (m_project == null)
			return;
			
		Composite area = parent;
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout(2, false));
	
		// row 0
		createTitleLabel(container, "Common:");
		
		// row 1
		new Label(container, SWT.NONE).setText("");	
		createLUSwitchButton(container);
		
		// row 2
		new Label(container, SWT.NONE).setText("Subnets: ");
		new Label(container, SWT.NONE).setText("(for discover make double click on the item of the table)");
		
		// row 3
		new Label(container, SWT.NONE).setText("");		
		createSubnetsTable(container);
		
		// row 4
		createTitleLabel(container, "Found devices:");
		
		// row 5
		new Label(container, SWT.NONE).setText("");	
		createDevicesTable(container);	
	}
	
	private void createLUSwitchButton(Composite container)
	{
		try
		{
			final Boolean isEnable = (Boolean)m_project.getSessionProperty(isLiveUpdateEnableTag);
			final Button  liveUpdateSwitchButton = new Button(container, SWT.TOGGLE);
			
			liveUpdateSwitchButton.setText(switchLUButtonText[liveUpdateEnableId]);		
			liveUpdateSwitchButton.setLayoutData(textAligment);
			liveUpdateSwitchButton.setSelection(isEnable != null ? isEnable.booleanValue() : false);
			liveUpdateSwitchButton.addSelectionListener(new SelectionListener()
			{
			    @Override
				public void widgetSelected(SelectionEvent e) 
				{
					Button btn = (Button)e.widget;
					
					try 
					{
						m_project.setSessionProperty(isLiveUpdateEnableTag, btn.getSelection());
//	sss
						LiveUpdateTask task = new LiveUpdateTask(m_project.getLocation(), btn.getSelection());
						Job taskJob = task.makeJob("live update is running", new LUJobNotifications(btn));
						taskJob.schedule();
						
						if (btn.getSelection())
						{
							btn.setText(switchLUButtonText[liveUpdateDisableId]);
						}
						else
						{
							btn.setText(switchLUButtonText[liveUpdateEnableId]);
						}
					} 
					catch (CoreException e1) 
					{
						e1.printStackTrace();
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		} 
		catch (CoreException e2) 
		{
			e2.printStackTrace();
		}
	}

	private void createDevicesTable(Composite container)
	{
		Composite tblContainer = new Composite(container, SWT.NONE);
		tblContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tblContainer.setLayout(new FillLayout());
		
		m_devicesTable = new Table(tblContainer, SWT.SINGLE | SWT.FULL_SELECTION);
		m_devicesTable.setHeaderVisible(true);
		m_devicesTable.setLinesVisible(true);
		
		TableColumn columnSubnetIp = new TableColumn(m_devicesTable, SWT.NONE);
		columnSubnetIp.setText("Device name");
		columnSubnetIp.setWidth(100);
		
		final TableColumn column2 = new TableColumn(m_devicesTable, SWT.CENTER);
		column2.setText("URI");
		column2.setWidth(200);

		final TableColumn column3 = new TableColumn(m_devicesTable, SWT.CENTER);
		column3.setText("Application name");
		column3.setWidth(200);
		
		final TableColumn column4 = new TableColumn(m_devicesTable, SWT.CENTER);
		column4.setText("Device platform");
		column4.setWidth(200);

		fillDevicesTable(container, m_devicesTable);
	}
	
	private void fillDevicesTable(Composite container, Table table)
	{
		new DevicesTableFillUIJob(m_project, table).schedule();
	}

	private void createSubnetsTable(Composite container) 
	{
		Composite tblContainer = new Composite(container, SWT.NONE);
		tblContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tblContainer.setLayout(new FillLayout());
		
		final Table subnetTable = new Table(tblContainer, SWT.SINGLE | SWT.FULL_SELECTION);
		subnetTable.setHeaderVisible(true);
		subnetTable.setLinesVisible(true);
		TableColumn columnSubnetIp = new TableColumn(subnetTable, SWT.NONE);
		columnSubnetIp.setText("Subnet");
		columnSubnetIp.setWidth(100);
		
		final TableColumn column2 = new TableColumn(subnetTable, SWT.CENTER);
		column2.setText("Status");
		column2.setWidth(300);

		fillSubnetsTable(container, subnetTable);
		
		subnetTable.addListener(SWT.MouseDoubleClick, new Listener()
		{			
			@Override
			public void handleEvent(Event event) 
			{
				dblClickHandler((TableItem)subnetTable.getSelection()[0]);
			}
		});
	}
	
	private void createTitleLabel(Composite container, String title)
	{
		new Label(container, SWT.NONE).setText(title);
		new Label(container, SWT.NONE).setText("");
	}

	private void dblClickHandler(TableItem item) 
	{
		new Thread(new SearchProgressMonitor(m_project, item.getText(0), item)).start();
	}
	
	private void fillSubnetsTable(Composite container, Table table)
	{
		table.clearAll();
		
		DiscoverSubnet discoverJob = new DiscoverSubnet(m_project);
		BusyIndicator.showWhile(container.getShell().getDisplay(), discoverJob);
				
		if (discoverJob.getSubnets() != null)
		{
			for (String itemName : discoverJob.getSubnets()) {
				 TableItem item = new TableItem(table, SWT.NONE);
				 item.setText(itemName);
				 item.setText(1, discoverStatus[emptyId]);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) 
	{
		fillDevicesTable(null, m_devicesTable);
	}
}
