package rhogenwizard.builder.rhodes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import rhogenwizard.PlatformType;

public class SelectPlatformDialog extends Dialog 
{
	private PlatformType m_selectPlaform = PlatformType.eUnknown;
	
	private static final int buttonWidht = 60;
	private static final int comboWidht = 300;
	
	private static final String[] platformTypes = {
		PlatformType.platformWinMobilePublic,
		PlatformType.platformAdroidPublic,
		PlatformType.platformBlackBerryPublic,
		PlatformType.platformIPhonePublic,
		PlatformType.platformWp7Public,
		PlatformType.platformSymbianPublic
	};
	 
	private Combo  m_platfromCombo = null;

	public SelectPlatformDialog(Shell parent) 
	{
		super(parent);
	}

	public PlatformType open() 
	{
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		shell.setText("Select platform");
		shell.setLayout(new GridLayout(1, true));
		
		RowData buttonAligment = new RowData(buttonWidht, SWT.DEFAULT);
		RowData comboAligment = new RowData(comboWidht, SWT.DEFAULT);
		// 1 row
		Label label = new Label(shell, SWT.NULL);
		label.setText("Please select:");
		
		// 2 row
		Composite rowContainer1 = new Composite(shell, SWT.NULL);
		rowContainer1.setLayout(new RowLayout());

		m_platfromCombo = new Combo(rowContainer1, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		m_platfromCombo.setItems(platformTypes);
		m_platfromCombo.select(0);
		m_platfromCombo.setLayoutData(comboAligment);
		
		// 3 row
		Composite rowContainer2 = new Composite(shell, SWT.CENTER);
		RowLayout rowLayout = new RowLayout();
		rowLayout.center = true;
		rowLayout.marginLeft = comboWidht / 2 - buttonWidht;
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

		// show dialog
		shell.pack();
		shell.open();
		
		Display display = parent.getDisplay();
		    
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		return m_selectPlaform;
	}
	
	protected void nandleCancel(Event event) 
	{
		m_selectPlaform = PlatformType.eUnknown;	
	}
	
	protected void handleOk(Event event) 
	{
		m_selectPlaform = PlatformType.fromString(m_platfromCombo.getText());		
	}	
}