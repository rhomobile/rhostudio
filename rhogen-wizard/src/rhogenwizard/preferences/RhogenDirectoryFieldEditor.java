package rhogenwizard.preferences;

import java.io.File;

import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

public class RhogenDirectoryFieldEditor extends StringButtonFieldEditor 
{
    private File filterPath = null;

    protected RhogenDirectoryFieldEditor() 
    {
    }

    /**
     * Creates a directory field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public RhogenDirectoryFieldEditor(String name, String labelText, Composite parent) 
    {
        init(name, labelText);
        setErrorMessage(JFaceResources
                .getString("DirectoryFieldEditor.errorMessage"));//$NON-NLS-1$
        setChangeButtonText(JFaceResources.getString("openBrowse"));//$NON-NLS-1$
        setValidateStrategy(VALIDATE_ON_FOCUS_LOST);
        createControl(parent);
    }

    /* (non-Javadoc)
     * Method declared on StringButtonFieldEditor.
     * Opens the directory chooser dialog and returns the selected directory.
     */
    protected String changePressed()
    {
        File f = new File(getTextControl().getText());
        if (!f.exists()) {
			f = null;
		}
        File d = getDirectory(f);
        if (d == null) {
			return null;
		}

        this.store();
        
        return d.getAbsolutePath();
    }

    /* (non-Javadoc)
     * Method declared on StringFieldEditor.
     * Checks whether the text input field contains a valid directory.
     */
    protected boolean doCheckState()
    {
        String fileName = getTextControl().getText();
        fileName = fileName.trim();
        if (fileName.length() == 0 && isEmptyStringAllowed()) {
			return true;
		}
        File file = new File(fileName);
        return true;
    }

    private File getDirectory(File startingDirectory) 
    {
        DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
        
        if (startingDirectory != null) 
        {
			fileDialog.setFilterPath(startingDirectory.getPath());
		}
        else if (filterPath != null) 
        {
        	fileDialog.setFilterPath(filterPath.getPath());
        }
        
        String dir = fileDialog.open();
      
        if (dir != null) 
        {
            dir = dir.trim();
            if (dir.length() > 0) {
				return new File(dir);
			}
        }

        return null;
    }

    /**
     * Sets the initial path for the Browse dialog.
     * @param path initial path for the Browse dialog
     * @since 3.6
     */
    public void setFilterPath(File path) 
    {
    	filterPath = path;
    }
    
    public void setVisiblePath(String path)
    {
    	getTextControl().setText(path);
    }
}
