package rhogenwizard.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

class DoubleValidator implements IInputValidator
{
	@Override
	public String isValid(String newText) {
		try
		{
			Double v = new Double(newText);
			return v.toString();
		}
		catch(NumberFormatException e)
		{
		}

		return "";
	}
}

public class RhogenComboFieldEditor extends FieldEditor
{
	private Button fNewVersin = null;
	private Combo fCombo;
	private String fValue;
	private String[][] fEntryNamesAndValues;
	private IItemAddedNotifier fNewValueNotify = null;
	
	public RhogenComboFieldEditor(String name, String labelText, String[][] entryNamesAndValues,
			Composite parent, IItemAddedNotifier notifier) 
	{
		init(name, labelText);
		Assert.isTrue(checkArray(entryNamesAndValues));
		fEntryNamesAndValues = entryNamesAndValues;
		fNewValueNotify = notifier;
		createControl(parent);		
	}

	private boolean checkArray(String[][] table) 
	{
		if (table == null)
		{
			return false;
		}
		
		for (int i = 0; i < table.length; i++) 
		{
			String[] array = table[i];
			if (array == null || array.length != 2) 
			{
				return false;
			}
		}
		
		return true;
	}

	protected void adjustForNumColumns(int numColumns)
	{
		if (numColumns > 1) 
		{
			Control control = getLabelControl();
			int left = numColumns;
			
			if (control != null) 
			{
				((GridData)control.getLayoutData()).horizontalSpan = 1;
				left = left - 1;
			}
		} 
		else
		{
			Control control = getLabelControl();
			
			if (control != null) 
			{
				((GridData)control.getLayoutData()).horizontalSpan = 1;
			}
			
			((GridData)fCombo.getLayoutData()).horizontalSpan = 1;			
		}
	}

	
	protected void doFillIntoGrid(final Composite parent, int numColumns) 
	{
		int comboC = 1;
		if (numColumns > 1) 
		{
			comboC = numColumns - 1;
		}
		
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		control = getComboBoxControl(parent);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		control.setLayoutData(gd);
		control.setFont(parent.getFont());
		
		fNewVersin = new Button(parent, SWT.PUSH);
		fNewVersin.setText("New Version");
		fNewVersin.setLayoutData(gd);
		fNewVersin.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Shell parentShell = parent.getShell();
				InputDialog inputDlg = new InputDialog(parentShell, "New version", 
						"New Blackberry SDK version", "", null);
				inputDlg.open();
				
				if (fNewValueNotify != null)
				{
					fNewValueNotify.addNewValue(inputDlg.getValue());
				}
				
				fCombo.add(inputDlg.getValue());

				super.widgetSelected(e);
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad() 
	{
		updateComboForValue(getPreferenceStore().getString(getPreferenceName()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() 
	{
		updateComboForValue(getPreferenceStore().getDefaultString(getPreferenceName()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() 
	{
		if (fValue == null) {
			getPreferenceStore().setToDefault(getPreferenceName());
			return;
		}
		getPreferenceStore().setValue(getPreferenceName(), fValue);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() 
	{
		return 3;
	}

	/*
	 * Lazily create and return the Combo control.
	 */
	private Combo getComboBoxControl(Composite parent)
	{
		if (fCombo == null) {
			fCombo = new Combo(parent, SWT.READ_ONLY);
			fCombo.setFont(parent.getFont());
			for (int i = 0; i < fEntryNamesAndValues.length; i++) {
				fCombo.add(fEntryNamesAndValues[i][0], i);
			}
			
			fCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					String oldValue = fValue;
					String name = fCombo.getText();
					fValue = getValueForName(name);
					setPresentsDefaultValue(false);
					fireValueChanged(VALUE, oldValue, fValue);					
				}
			});
		}
		return fCombo;
	}
	
	/*
	 * Given the name (label) of an entry, return the corresponding value.
	 */
	private String getValueForName(String name) 
	{
		for (int i = 0; i < fEntryNamesAndValues.length; i++) {
			String[] entry = fEntryNamesAndValues[i];
			if (name.equals(entry[0])) {
				return entry[1];
			}
		}
		return fEntryNamesAndValues[0][0];
	}
	
	/*
	 * Set the name in the combo widget to match the specified value.
	 */
	private void updateComboForValue(String value) 
	{
		fValue = value;
		for (int i = 0; i < fEntryNamesAndValues.length; i++) {
			if (value.equals(fEntryNamesAndValues[i][1])) {
				fCombo.setText(fEntryNamesAndValues[i][0]);
				return;
			}
		}
		if (fEntryNamesAndValues.length > 0) {
			fValue = fEntryNamesAndValues[0][1];
			fCombo.setText(fEntryNamesAndValues[0][0]);
		}
	}

	public void setEnabled(boolean enabled, Composite parent) 
	{
		super.setEnabled(enabled, parent);
		getComboBoxControl(parent).setEnabled(enabled);
	}
	
	public void setSelectionListener(SelectionListener listener)
	{
		if (fCombo != null)
		{
			fCombo.addSelectionListener(listener);
		}
	}
	
	public Combo getCombo()
	{
		return fCombo;
	}
	
	public void addNewComboValue(String value)
	{
		if (fCombo != null)
		{
			fCombo.add(value);
		}
	}
}
