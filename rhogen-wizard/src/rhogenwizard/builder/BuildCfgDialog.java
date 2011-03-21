package rhogenwizard.builder;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BuildCfgDialog extends Dialog 
{
	//public 
	/**
	 * @param parent
	 */
	public BuildCfgDialog(Shell parent) 
	{
		super(parent);
	}

	/**
	 * Makes the dialog visible.
	 * 
	 * @return
	 */
	public int open()
	{
		Shell parent = getParentShell();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		shell.setText("NumberInputDialog");

		shell.setLayout(new GridLayout(2, true));

		Label label = new Label(shell, SWT.NULL);
		label.setText("Please enter a valid number:");

		final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);

		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText("Ok");
		buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");

		text.addListener(SWT.Modify, new Listener() 
		{
			public void handleEvent(Event event) 
			{
				try 
				{
					//value = new Double(text.getText());
					buttonOK.setEnabled(true);
				}
				catch (Exception e) 
				{
					buttonOK.setEnabled(false);
				}
			}
	    });

	    buttonOK.addListener(SWT.Selection, new Listener() 
	    {
	    	public void handleEvent(Event event) 
	    	{
	    		shell.dispose();
	    	}
	    });

	    buttonCancel.addListener(SWT.Selection, new Listener() 
	    {
	    	public void handleEvent(Event event) 
	    	{
	    		//value = null;
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

	    text.setText("");
	    shell.pack();
	    shell.open();

	    Display display = parent.getDisplay();
	    
	    while (!shell.isDisposed()) 
	    {
	    	if (!display.readAndDispatch())
	    		display.sleep();
	    }
	    
		return 0;
	  }

	  /*
	  public static void main(String[] args) 
	  {
	    Shell shell = new Shell();
	    NumberInputDialog dialog = new NumberInputDialog(shell);
	    System.out.println(dialog.open());
	  }
	  */
	}
