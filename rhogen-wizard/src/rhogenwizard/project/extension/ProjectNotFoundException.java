package rhogenwizard.project.extension;

public class ProjectNotFoundException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3522478200371875737L;
	
	String m_projectName = null;
	
	public ProjectNotFoundException(final String projectName)
	{
		m_projectName = projectName;
	}
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Project ");
		sb.append(m_projectName); 		
		sb.append(" not found in workspace.");		
		return sb.toString();
	}
}
