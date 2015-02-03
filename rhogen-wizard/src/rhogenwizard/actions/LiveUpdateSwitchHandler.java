package rhogenwizard.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

import rhogenwizard.DialogUtils;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.sdk.task.liveupdate.LiveUpdateSwitchTask;

public class LiveUpdateSwitchHandler extends AbstractHandler implements IHandler 
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		Event    baseEvent = (Event)event.getTrigger();
		ToolItem item      = (ToolItem)baseEvent.widget;

		IProject project = ProjectFactory.getInstance().getSelectedProject();

//		if (project == null)
//			DialogUtils.information("Information", "Project was not selected, before");
		
		if (item.getSelection() == true)
		{
//			if ()
//			LiveUpdateSwitchTask task = new LiveUpdateSwitchTask(workDir, isEnable)
//			DialogUtils.information("", "live update enable");
		}
		else
		{
			DialogUtils.information("", "live update disable");
		}

		return null;
	}
}
