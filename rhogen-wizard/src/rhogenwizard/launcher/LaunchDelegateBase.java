package rhogenwizard.launcher;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.BuildType;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.DialogUtils;
import rhogenwizard.LogFileHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.PlatformType;
import rhogenwizard.ProcessListViewer;
import rhogenwizard.RhodesConfigurationRO;
import rhogenwizard.RunType;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.model.DebugTarget;
import rhogenwizard.rhohub.TokenChecker;
import rhogenwizard.sdk.task.CleanPlatformTask;
import rhogenwizard.sdk.task.IDebugTask;
import rhogenwizard.sdk.task.IRunTask;
import rhogenwizard.sdk.task.RunTask.StoppedException;
import rhogenwizard.sdk.task.run.LocalDebugRhodesAppTask;
import rhogenwizard.sdk.task.run.LocalRunRhodesAppTask;
import rhogenwizard.sdk.task.run.RhohubDebugRhodesAppTask;
import rhogenwizard.sdk.task.run.RhohubRunRhodesAppTask;

/////////////////////////////////////////////////////////////////////////

class FailBuildException extends Exception
{
	private static final long serialVersionUID = 5907642700379669820L;
}

///////////////////////////////////////////////////////////////////////////

class BuildProjectAsRelease
{
	private IProject     m_currProject  = null;
	private RunType      m_selType      = null;
	private BuildType    m_buildType    = null;
	private PlatformType m_platformType = null;
	private boolean      m_isReloadCode = false;
	private boolean      m_isTrace      = false;
	
	private String      m_startPathOverride        = null;
	private String[]    m_additionalRubyExtensions = null;
	
	final IProgressMonitor m_monitor;
	
	public BuildProjectAsRelease(RhodesConfigurationRO configuration, ILaunch launch, String startPathOverride, String[] additionalRubyExtensions, final IProgressMonitor monitor)
	{
		m_platformType             = configuration.platformType();
		m_buildType                = configuration.buildType();
		m_isReloadCode             = configuration.reloadCode();
		m_isTrace                  = configuration.trace();
		m_selType                  = configuration.runType();
		m_startPathOverride        = startPathOverride;
		m_additionalRubyExtensions = additionalRubyExtensions;
		m_monitor                  = monitor;
		
		m_currProject = ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.project());		
	}
	
	public boolean xcall() throws InterruptedException
	{
		m_monitor.setTaskName("Build release configuration of project " + m_currProject.getName());
		
        Activator activator = Activator.getDefault();
        activator.killProcessesForForRunReleaseRhodesAppTask();

        ProcessListViewer rhosims = new ProcessListViewer("/RhoSimulator/rhosimulator.exe \"-approot=\'");

        boolean buildResult = runSelectedBuildConfiguration(m_currProject, m_selType);
        
        if (buildResult)
        {
            activator.storeProcessesForForRunReleaseRhodesAppTask(rhosims.getNewProcesses());
        }

		return buildResult;
	}	
	
	private boolean runSelectedBuildConfiguration(IProject currProject, RunType selType)
	{
		if (!TokenChecker.processToken(currProject))
			return false;
		
        IRunTask task;
		if (m_buildType == BuildType.eRhoMobileCom) {
            task = new RhohubRunRhodesAppTask(currProject.getLocation().toOSString(),
                m_platformType, selType, m_isTrace, m_startPathOverride,
                m_additionalRubyExtensions);
		}
		else 
		{
            task = new LocalRunRhodesAppTask(currProject.getLocation().toOSString(),
                m_platformType, selType, m_isReloadCode, m_isTrace,
                m_startPathOverride, m_additionalRubyExtensions);
		}

		task.run(m_monitor);

		return task.isOk();
	}
}

///////////////////////////////////////////////////////////////////////////

public abstract class LaunchDelegateBase extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{
	private static LogFileHelper      rhodesLogHelper = new LogFileHelper();
	
	protected String          m_projectName   = null;
	private PlatformType      m_platformType  = null;   
	private boolean           m_isClean       = false;
	private final String      m_startPathOverride;
	private final String[]    m_additionalRubyExtensions;
	
	public LaunchDelegateBase(String startPathOverride, String[] additionalRubyExtensions)
	{
	    m_startPathOverride        = startPathOverride;
	    m_additionalRubyExtensions = additionalRubyExtensions;
	}
	
	protected void setupConfigAttributes(ILaunchConfiguration configuration) throws CoreException
	{	    
		RhodesConfigurationRO rc = new RhodesConfigurationRO(configuration);
		
		m_projectName  = rc.project();
		m_platformType = rc.platformType();
		m_isClean      = rc.clean();
	}

    public synchronized void launchLocalProject(IProject project, ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
    {
        try
        {         	
            rhodesLogHelper.stopLog();

            setStandartConsoleOutputIsOff();

            ConsoleHelper.getBuildConsole().clear();
            ConsoleHelper.getBuildConsole().show();

            setupConfigAttributes(configuration);

            RunType runType = getRunType(configuration);

            if (m_projectName == null || m_projectName.length() == 0 || runType.id == null) 
            {
                throw new IllegalArgumentException("Platform and project name should be assigned");
            }

            if (!project.isOpen()) 
            {
                throw new IllegalArgumentException("Project " + project.getName() + " not found");
            }

            RhodesConfigurationRO rc = new RhodesConfigurationRO(configuration);
            
            try 
            {
                cleanProject(project, m_isClean, m_platformType, monitor);
            	
                if (mode.equals(ILaunchManager.DEBUG_MODE))
                {
                    ShowPerspectiveJob job = new ShowPerspectiveJob("show debug perspective", DebugConstants.debugPerspectiveId);
                    job.schedule();

                    try 
                    {
                        OSHelper.killProcess("rhosimulator");
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					
					IProcess debugProcess = buildProjectAsDebug(rc, launch, m_startPathOverride, m_additionalRubyExtensions, monitor);
					
					if(!debugProcess.isTerminated())
					{
						DebugTarget target = new DebugTarget(launch, null, project, runType, m_platformType);
						target.setProcess(debugProcess);
						launch.addDebugTarget(target);						
					}
                }
                else
                {
                    BuildProjectAsRelease b = new BuildProjectAsRelease(rc, launch,  m_startPathOverride, m_additionalRubyExtensions, monitor);
                    if (!b.xcall())
                    {
                        throw new FailBuildException();
                    }
                }
            }
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            } 
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (FailBuildException e)
            {
                e.printStackTrace();
            }
            catch (StoppedException e)
            {
            }

            monitor.done();
        }
        catch (IllegalArgumentException e) 
        {
            DialogUtils.error("Error", e.getMessage());
        }
    }

    void setStandartConsoleOutputIsOff()
    {
        IPreferenceStore prefs = DebugUIPlugin.getDefault().getPreferenceStore();

        prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
        prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
        prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
        prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
	}

    @Override
    public void handleDebugEvents(DebugEvent[] events)
    {
    }

    private static RunType getRunType(ILaunchConfiguration configuration)
    {
        return new RhodesConfigurationRO(configuration).runType();
    }
    
    private static void cleanProject(IProject project, boolean isClean,
        PlatformType platformType, IProgressMonitor monitor)
    {
        monitor.setTaskName("Run clean project build files");
        
        if (isClean)
        {
            new CleanPlatformTask(project.getLocation().toOSString(), platformType).run(monitor);
        }
    }

    public static IProcess buildProjectAsDebug(RhodesConfigurationRO configuration, ILaunch launch,
        String startPathOverride, String[] additionalRubyExtensions, IProgressMonitor monitor)
            throws IOException
    {
        PlatformType platformType = configuration.platformType();
        BuildType    buildType    = configuration.buildType();
        boolean      reloadCode   = configuration.reloadCode();
        boolean      trace        = configuration.trace();
        RunType      runType      = configuration.runType();
        IProject     project      = ResourcesPlugin.getWorkspace().getRoot().getProject(
            configuration.project());

        String projectName = project.getName();
        String projectDir  = project.getLocation().toOSString();

        monitor.setTaskName("Build debug configuration of project " + projectName);

        if (!TokenChecker.processToken(project))
        {
            return null;
        }

        IDebugTask task;
        if (buildType == BuildType.eRhoMobileCom)
        {
            task = new RhohubDebugRhodesAppTask(launch, runType, projectDir, projectName,
                platformType, reloadCode, startPathOverride, additionalRubyExtensions)
            .sync();
        }
        else
        {
            task = new LocalDebugRhodesAppTask(launch, runType, projectDir, projectName,
                platformType, reloadCode, trace, startPathOverride, additionalRubyExtensions)
            .sync();
        }

        task.run(monitor);

        return task.getDebugProcess();
    }
}
