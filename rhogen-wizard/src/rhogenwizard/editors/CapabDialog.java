package rhogenwizard.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import rhogenwizard.buildfile.AppYmlFile;


public class CapabDialog extends Dialog 
{
	 List<String> capabList = null;

	 private static final int buttonWidht = 60;
	 private static final String[] capabTypes = {
		 "gps",
		 "pim",
		 "camera",
		 "vibrate",
		 "phone",
		 "bluetooth",
		 "calendar",
		 "motorola"
	 };
	 
	  private Table      m_capabTable = null;
	  private AppYmlFile m_ymlFile = null;
	  /**
	   * @param parent
	   */
	  public CapabDialog(Shell parent, AppYmlFile ymlFile) 
	  {
		  super(parent);
		  
		  m_ymlFile = ymlFile;
	  }

	  /**
	   * @param parent
	   * @param style
	   */
	  public CapabDialog(Shell parent, int style) 
	  {
		  super(parent, style);
	  }

	  public List<String> open() 
	  {
		  Shell parent = getParent();
		  final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		  shell.setText("Select capabilities");
		  shell.setLayout(new GridLayout(1, true));
		
		  RowData buttonAligment = new RowData(buttonWidht, SWT.DEFAULT);
		    
		  // 1 row
		  Label label = new Label(shell, SWT.NULL);
		  label.setText("Please select:");
		
		  // 2 row
		  Composite rowContainer1 = new Composite(shell, SWT.NULL);
		  rowContainer1.setLayout(new RowLayout());
		    
		  m_capabTable = new Table(rowContainer1, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		  m_capabTable.setLayoutData(new RowData(200, 300));
		    		
		  // 3 row
		  Composite rowContainer2 = new Composite(shell, SWT.NULL);
		  RowLayout rowLayout = new RowLayout();
		  rowLayout.center = true;
		  rowContainer2.setLayout(rowLayout);
		    
		  // event handlers
		  final Button buttonOK = new Button(rowContainer2, SWT.PUSH);
		  buttonOK.setText("Ok");
		  buttonOK.setLayoutData(buttonAligment);
		    
		  final Button buttonCancel = new Button(rowContainer2, SWT.PUSH);
		  buttonCancel.setText("Cancel");
		  buttonCancel.setLayoutData(buttonAligment);
		
		  buttonOK.addListener(SWT.Selection, new Listener() 
		  {
			  public void handleEvent(Event event) 
			  {
				  handleOk(event);
				  shell.dispose();
			  }
		  });
		
		  buttonCancel.addListener(SWT.Selection, new Listener() 
		  {
			  public void handleEvent(Event event) 
			  {
				  nandleCancel(event);			    	  
				  shell.dispose();
			  }
		  });
		    
		  shell.addListener(SWT.Traverse, new Listener() 
		  {
			      public void handleEvent(Event event) 
			      {
			        if(event.detail == SWT.TRAVERSE_ESCAPE)
			          event.doit = false;
			      }
		  });
		
		  // init
		  List<String> selCapabList = null;
		  
		  if (m_ymlFile != null) {
			  selCapabList = m_ymlFile.getCapabilities();
		  }
		  
		  for (int i = 0; i < capabTypes.length; i++) 
		  {
			  String currItemText = capabTypes[i];
			  
			  TableItem item = new TableItem(m_capabTable, SWT.NONE);
			  item.setText(currItemText);
			  
			  if (selCapabList != null) {
				  item.setChecked(selCapabList.contains(currItemText));
			  }
		  }

		  // show dialog
		  shell.pack();
		  shell.open();
		
		  Display display = parent.getDisplay();
		    
		  while (!shell.isDisposed()) 
		  {
			      if (!display.readAndDispatch())
			        display.sleep();
		  }
		
		  return capabList;
	}

	protected void nandleCancel(Event event) 
	{
		capabList = null;	
	}

	protected void handleOk(Event event) 
	{
		int itemsCount = m_capabTable.getItemCount();
		capabList = new ArrayList<String>();
		
		for (int i=0; i<itemsCount; ++i)
		{
			TableItem item = m_capabTable.getItem(i);
			
			if (item.getChecked())
			{
				capabList.add(item.getText());
			}
		}
	}	
}
