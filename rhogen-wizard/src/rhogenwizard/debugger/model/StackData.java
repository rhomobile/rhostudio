package rhogenwizard.debugger.model;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.debugger.DebugVariable;

public class StackData
{
	public StackData(String resName, int line)
	{
		m_resName       = resName;
		m_codeLine      = line;
		m_currVariables = null;
	}
	
	public String              m_resName;
	public int                 m_codeLine;
	public List<DebugVariable> m_currVariables;
}