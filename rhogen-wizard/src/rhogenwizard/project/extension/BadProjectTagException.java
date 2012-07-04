package rhogenwizard.project.extension;

public class BadProjectTagException extends Exception 
{
	private Class<?> m_tag = null;

	private static final long serialVersionUID = 2576187748604698201L;
	
	public BadProjectTagException(Class<?> tag)
	{
		m_tag = tag; 	
	}
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Project with tag ");
		m_tag.toString();		
		sb.append(" can't create in project factory.");
		
		return sb.toString();
	}
}
