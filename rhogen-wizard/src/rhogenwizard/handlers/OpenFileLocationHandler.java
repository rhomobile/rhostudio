package rhogenwizard.handlers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.internal.ObjectPluginAction;

import rhogenwizard.OSHelper;

public class OpenFileLocationHandler extends ActionDelegate implements IActionDelegate 
{

	@Override
	public void run(IAction action) 
	{
		ObjectPluginAction actionObject = (ObjectPluginAction)action;
		IStructuredSelection selObject  = (IStructuredSelection)actionObject.getSelection();
		IFile selFile  = (IFile)selObject.getFirstElement();
		IPath filePath = selFile.getLocation();
		
		List<String> cloneSegList = new ArrayList<String>(Arrays.asList(filePath.segments()));
		cloneSegList.remove(cloneSegList.size()-1);
		
		IPath newPath = (IPath) new Path(filePath.getDevice() + Path.SEPARATOR);

		for (String s : cloneSegList) {
			newPath = newPath.append(s);
		}
		
		try 
		{
			OSHelper.openFolder(newPath);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		super.run(action);
	}
}
