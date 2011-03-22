package rhogenwizard.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;

import rhogenwizard.RhodesAdapter;
import rhogenwizard.ConsoleHelper;

public class RhogenBuilder extends IncrementalProjectBuilder 
{
	public  static final String BUILDER_ID = "Rhomobile.rhogenBuilder";

	private RhodesAdapter m_rhodeAdapter = new RhodesAdapter();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException 
	{ 
		try 
		{
			String platformName = (String) getProject().getSessionProperty(getPlatformQualifier());
			
			if (kind != CLEAN_BUILD && platformName != null)
			{
				ConsoleHelper.consolePrint("Build project " + getProject().getName() + " for platform " + platformName);
				m_rhodeAdapter.buildApp(getProject().getLocation().toOSString(), platformName);
			}
			
			/*
			if (kind == FULL_BUILD) 
			{
				fullBuild(monitor);
			}
			else
			{
				IResourceDelta delta = getDelta(getProject());
				
				if (delta == null) 
				{
					fullBuild(monitor);
				}
				else
				{
					incrementalBuild(delta, monitor);
				}
			}
			*/
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return null;
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
