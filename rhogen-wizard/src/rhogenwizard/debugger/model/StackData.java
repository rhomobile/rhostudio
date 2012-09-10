package rhogenwizard.debugger.model;

import java.util.List;

public class StackData
{
	public String              m_resName;
	public int                 m_codeLine;
	public List<rhogenwizard.debugger.backend.DebugVariable> m_currVariables;

	public StackData(String resName, int line)
	{
		m_resName       = resName;
		m_codeLine      = line;
		m_currVariables = null;
	}
}