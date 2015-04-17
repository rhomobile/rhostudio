package rhogenwizard.launcher;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
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


public class LaunchDelegateBase extends LaunchConfigurationDelegate
{
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	private final String      m_startPathOverride;
	private final String[]    m_additionalRubyExtensions;
	
	public LaunchDelegateBase(String startPathOverride, String... additionalRubyExtensions)
	{
	    m_startPathOverride        = startPathOverride;
	    m_additionalRubyExtensions = additionalRubyExtensions;
	}
	
    @Override
    public synchronized void launch(ILaunchConfiguration configuration, String mode,
        ILaunch launch, IProgressMonitor monitor)
    {
        RhodesConfigurationRO rc = new RhodesConfigurationRO(configuration);

        String       projectName  = rc.project();
        PlatformType platformType = rc.platformType();
        boolean      clean        = rc.clean();

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        if (!TokenChecker.processToken(project))
        {
            return;
        }

        try
        {         	
            rhodesLogHelper.stopLog();

            setStandartConsoleOutputIsOff();

            ConsoleHelper.getBuildConsole().clear();
            ConsoleHelper.getBuildConsole().show();

            RunType runType = getRunType(configuration);

            if (projectName == null || projectName.length() == 0 || runType.id == null)
            {
                throw new IllegalArgumentException("Platform and project name should be assigned");
            }

            if (!project.isOpen()) 
            {
                throw new IllegalArgumentException("Project " + project.getName() + " not found");
            }

            try 
            {
                cleanProject(project, clean, platformType, monitor);

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

                    IDebugTask task = buildProjectAsDebug(rc, project, launch, monitor);
                    if (task.isOk())
                    {
                        IProcess debugProcess = task.getDebugProcess();
                        if (!debugProcess.isTerminated())
                        {
                            DebugTarget target = new DebugTarget(launch, null, project, runType,
                                platformType);
                            target.setProcess(debugProcess);
                            launch.addDebugTarget(target);
                        }
                    }
                }
                else
                {
                    buildProjectAsRelease(rc, project, launch, monitor);
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

	private void setStandartConsoleOutputIsOff()
    {
        IPreferenceStore prefs = DebugUIPlugin.getDefault().getPreferenceStore();

        prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
        prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
        prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
        prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
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

    public IDebugTask buildProjectAsDebug(RhodesConfigurationRO configuration, IProject project,
        ILaunch launch, IProgressMonitor monitor)
            throws IOException
    {
        PlatformType platformType = configuration.platformType();
        BuildType    buildType    = configuration.buildType();
        boolean      reloadCode   = configuration.reloadCode();
        boolean      trace        = configuration.trace();
        RunType      runType      = configuration.runType();

        String projectName = project.getName();
        String projectDir  = project.getLocation().toOSString();

        monitor.setTaskName("Build debug configuration of project " + projectName);

        IDebugTask task;
        if (buildType == BuildType.eRhoMobileCom)
        {
            task = new RhohubDebugRhodesAppTask(launch, runType, projectDir, projectName,
                platformType, reloadCode, m_startPathOverride, m_additionalRubyExtensions);
        }
        else
        {
            task = new LocalDebugRhodesAppTask(launch, runType, projectDir, projectName,
                platformType, reloadCode, trace, m_startPathOverride, m_additionalRubyExtensions);
        }

        task.run(monitor);

        return task;
    }

    private void buildProjectAsRelease(RhodesConfigurationRO configuration, IProject project,
        ILaunch launch, IProgressMonitor monitor)
            throws InterruptedException
    {
        PlatformType platformType = configuration.platformType();
        BuildType    buildType    = configuration.buildType();
        boolean      reloadCode   = configuration.reloadCode();
        boolean      trace        = configuration.trace();
        RunType      runType      = configuration.runType();

        monitor.setTaskName("Build release configuration of project " + project.getName());

        Activator activator = Activator.getDefault();
        activator.killProcessesForForRunReleaseRhodesAppTask();

        ProcessListViewer rhosims = new ProcessListViewer("/RhoSimulator/rhosimulator.exe \"-approot=\'");

        IRunTask task;
        if (buildType == BuildType.eRhoMobileCom) {
            task = new RhohubRunRhodesAppTask(project.getLocation().toOSString(),
                platformType, runType, trace, m_startPathOverride,
                m_additionalRubyExtensions);
        }
        else
        {
            task = new LocalRunRhodesAppTask(project.getLocation().toOSString(),
                platformType, runType, reloadCode, trace,
                m_startPathOverride, m_additionalRubyExtensions);
        }

        task.run(monitor);

        if (task.isOk())
        {
            activator.storeProcessesForForRunReleaseRhodesAppTask(rhosims.getNewProcesses());
        }
    }
}
