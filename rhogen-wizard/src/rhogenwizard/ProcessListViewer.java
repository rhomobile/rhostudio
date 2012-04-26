package rhogenwizard;

import java.util.HashSet;
import java.util.Set;

public class ProcessListViewer
{
    private final String m_commandLineFragment;
    private final Set<Integer> m_initialProcessList;

    public ProcessListViewer(String commandLineFragment) throws InterruptedException
    {
        m_commandLineFragment = commandLineFragment;
        m_initialProcessList = OSHelper.getProcessesIds(commandLineFragment);
    }

    public Set<Integer> getNewProcesses() throws InterruptedException
    {
        Set<Integer> currentProcessList = OSHelper.getProcessesIds(m_commandLineFragment);

        Set<Integer> newProcesses = new HashSet<Integer>(currentProcessList);
        newProcesses.removeAll(m_initialProcessList);
        return newProcesses;
    }
}
