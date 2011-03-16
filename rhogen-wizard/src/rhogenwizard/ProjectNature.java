package rhogenwizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;

import org.eclipse.core.runtime.CoreException;

public class ProjectNature implements IProjectNature 
{
	IProject m_project = null;
    public static final String NATURE_ID = "customplugin.projectNature"; //$NON-NLS-1$

    @Override
    public void configure() throws CoreException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deconfigure() throws CoreException {
        // TODO Auto-generated method stub
    }

    @Override
    public IProject getProject() {
        return m_project;
    }

    @Override
    public void setProject(IProject project) {
    	m_project = project;
    }

}
