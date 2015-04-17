package rhogenwizard.launcher;

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
        RunType      runType      = rc.runType();
        boolean      clean        = rc.clean();

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        if (!TokenChecker.processToken(project))
        {
            return;
        }

        if (projectName == null || projectName.length() == 0 || runType.id == null)
        {
            DialogUtils.error("Error", "Platform and project name should be assigned");
            return;
        }

        if (!project.isOpen())
        {
            DialogUtils.error("Error", "Project " + project.getName() + " not found");
            return;
        }

        rhodesLogHelper.stopLog();

        setStandartConsoleOutputIsOff();

        ConsoleHelper.getBuildConsole().clear();
        ConsoleHelper.getBuildConsole().show();

        try
        {
            if (rc.clean())
            {
                monitor.setTaskName("Run clean project build files");
                new CleanPlatformTask(project.getLocation().toOSString(), platformType).run(monitor);
            }

            if (mode.equals(ILaunchManager.DEBUG_MODE))
            {
                new ShowPerspectiveJob("show debug perspective", DebugConstants.debugPerspectiveId)
                .schedule();

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
                        launch.addDebugTarget(new DebugTarget(launch, debugProcess, project,
                            runType, platformType));
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
        catch (StoppedException e)
        {
        }

        monitor.done();
    }

    private void setStandartConsoleOutputIsOff()
    {
        IPreferenceStore prefs = DebugUIPlugin.getDefault().getPreferenceStore();

        prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
        prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
        prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
        prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
    }

    public IDebugTask buildProjectAsDebug(RhodesConfigurationRO configuration, IProject project,
        ILaunch launch, IProgressMonitor monitor)
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

        String projectDir  = project.getLocation().toOSString();
        
        monitor.setTaskName("Build release configuration of project " + project.getName());

        Activator activator = Activator.getDefault();
        activator.killProcessesForForRunReleaseRhodesAppTask();

        ProcessListViewer rhosims = new ProcessListViewer("/RhoSimulator/rhosimulator.exe \"-approot=\'");

        IRunTask task;
        if (buildType == BuildType.eRhoMobileCom) {
            task = new RhohubRunRhodesAppTask(projectDir, platformType, runType, trace,
                m_startPathOverride, m_additionalRubyExtensions);
        }
        else
        {
            task = new LocalRunRhodesAppTask(projectDir, platformType, runType, reloadCode,
                trace, m_startPathOverride, m_additionalRubyExtensions);
        }

        task.run(monitor);

        if (task.isOk())
        {
            activator.storeProcessesForForRunReleaseRhodesAppTask(rhosims.getNewProcesses());
        }
    }
}
