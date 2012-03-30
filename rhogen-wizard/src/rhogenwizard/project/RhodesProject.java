package rhogenwizard.project;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ruby.core.RubyNature;

import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.YmlFile;
import rhogenwizard.project.extension.ProjectNotFoundException;
import rhogenwizard.project.nature.RhodesNature;

public class RhodesProject extends RhomobileProject 
{
	public RhodesProject(IProject project)
	{
		m_project = project;
	}
	
	public static boolean checkNature(IProject otherProject) 
	{
		if (otherProject == null)
			return false;
		
		try 
		{
			IProjectNature nature = otherProject.getNature(RhodesNature.natureId);
		
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
	public boolean checkProject() throws ProjectNotFoundException
	{
		if (m_project == null)
    		throw new ProjectNotFoundException("");

		File projectDir = new File(m_project.getLocation().toOSString());
		
		if (!projectDir.isDirectory() || !projectDir.exists())
		{
			return false;
		}

		File buildFile = new File(projectDir.getAbsolutePath() + File.separator + AppYmlFile.configFileName);
		
		if (!buildFile.exists())
		{
			return false;
		}
		
		return true;
	}

	@Override
	public void addNature() throws CoreException, ProjectNotFoundException 
	{
		IProjectDescription description = getProject().getDescription();
		String[] natures = description.getNatureIds();

		// Add the nature
		String[] newNatures = new String[natures.length + 2];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length]   = RhodesNature.natureId;		
		newNatures[natures.length+1] = RubyNature.NATURE_ID; // set nature from dltk ruby plugin
		
		description.setNatureIds(newNatures);
		getProject().setDescription(description, null);
	}

	@Override
	public YmlFile getSettingFile() throws ProjectNotFoundException, FileNotFoundException 
	{
		if (m_project == null)
			throw new ProjectNotFoundException("");
		
		return AppYmlFile.createFromProject(m_project);
	}
}
