package rhogenwizard.project;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import rhogenwizard.project.extension.ProjectNotFoundExtension;
import rhogenwizard.project.nature.RhoconnectNature;
import rhogenwizard.project.nature.RhodesNature;

public class RhoconnectProject extends RhomobileProject 
{
	static final String configFile = "config.ru"; 
	
	public RhoconnectProject(IProject project)
	{
		m_project = project;
	}

	@Override
	public boolean checkProject() throws ProjectNotFoundExtension
	{
		if (m_project == null)
    		throw new ProjectNotFoundExtension("");

		File projectDir = new File(m_project.getLocation().toOSString());
		
		if (!projectDir.isDirectory() || !projectDir.exists())
		{
			return false;
		}

		File buildFile = new File(projectDir.getAbsolutePath() + File.separator + configFile);
		
		if (!buildFile.exists())
		{
			return false;
		}
		
		return true;
	}

	public static boolean checkNature(IProject otherProject) 
	{
		try 
		{
			IProjectNature nature = otherProject.getNature(RhoconnectNature.natureId);
		
			if (nature != null)
				return true;		
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}

		return false;
	}


	@Override
	public void addNature() throws CoreException, ProjectNotFoundExtension 
	{
		IProjectDescription description = getProject().getDescription();
		String[] natures = description.getNatureIds();

		// Add the nature
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = RhoconnectNature.natureId;
		description.setNatureIds(newNatures);
		getProject().setDescription(description, null);
	}
}
