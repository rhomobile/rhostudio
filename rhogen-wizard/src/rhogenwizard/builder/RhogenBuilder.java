package rhogenwizard.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;

import rhogenwizard.RhodesAdapter;
import rhogenwizard.ConsoleHelper;

public class RhogenBuilder extends IncrementalProjectBuilder 
{
	public  static final String BUILDER_ID = "com.rhomobile.eclipse.rhogenBuilder";

	private RhodesAdapter m_rhodeAdapter = new RhodesAdapter();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, final IProgressMonitor monitor) throws CoreException 
	{ 
		try 
		{			
			String platformName = (String) getProject().getSessionProperty(getPlatformQualifier());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException 
	{
		try 
		{
			ConsoleHelper.consolePrint("Clean project started");
			m_rhodeAdapter.cleanApp(getProject().getLocation().toOSString());
			ConsoleHelper.consolePrint("Clean application cancelled");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		super.clean(monitor);
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException 
	{
		try 
		{
			getProject().accept(new RhogenResourceVisitor());
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException 
	{
		// the visitor does the work.
		delta.accept(new RhogenDeltaVisitor());
	}
	
	public static QualifiedName getPlatformQualifier()
	{
		return new QualifiedName("buider", "platform=");
	}
}
