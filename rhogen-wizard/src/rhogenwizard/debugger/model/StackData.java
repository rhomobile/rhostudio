package rhogenwizard.debugger.model;

import java.util.ArrayList;
import java.util.List;

public class StackData
{
	public StackData(String resName, int line)
	{
		m_resName       = resName;
		m_codeLine      = line;
		m_currVariables = new ArrayList<String>();
	}
	
	public String       m_resName;
	public int          m_codeLine;
	public List<String> m_currVariables;
}