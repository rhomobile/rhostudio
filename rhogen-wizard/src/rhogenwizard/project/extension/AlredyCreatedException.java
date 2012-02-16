package rhogenwizard.project.extension;

import org.eclipse.core.resources.IProject;

public class AlredyCreatedException extends Exception 
{
	private static final long serialVersionUID = 2517636573612234997L;

	private IProject m_project = null;
	
	public AlredyCreatedException(IProject project)
	{
		m_project = project;
	}
	
	@Override
	public String getMessage() 
	{
		return this.toString();
	}

	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Project ");
		
		if (m_project != null)
		{
			sb.append(m_project.getName());
		}
		
		sb.append(" with this name is already created.");
		
		return sb.toString();
	}
}
