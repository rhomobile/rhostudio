package rhogenwizard;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rhogenwizard.sdk.task.StopSyncAppTask;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
    // The plug-in ID
    public static final String PLUGIN_ID    = "RhogenWizard";        //$NON-NLS-1$
    public static final String egitPluginId = "org.eclipse.egit.ui";        //$NON-NLS-1$
    
    // The shared instance
    private static Activator plugin;

    private Set<Integer> runningProcessesIdsForRunReleaseRhodesAppTask = new HashSet<Integer>();

    /**
     * The constructor
     */
    public Activator()
    {
    }

    private void setEgitDefaultRepositaryPath()
    {
        // get object which represents the workspace
        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        // get location of workspace (java.io.File)
        File workspaceDirectory = workspace.getRoot().getLocation().toFile();

        IEclipsePreferences instanceNode = InstanceScope.INSTANCE.getNode(egitPluginId);
        instanceNode.put(UIPreferences.DEFAULT_REPO_DIR, workspaceDirectory.toString());
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;

        // set our default 
        setEgitDefaultRepositaryPath();

        ConsoleHelper.initialize();
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        killProcessesForForRunReleaseRhodesAppTask();

        new StopSyncAppTask().run();

        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public void storeProcessesForForRunReleaseRhodesAppTask(Set<Integer> pids)
    {
        runningProcessesIdsForRunReleaseRhodesAppTask = pids;
    }

    public void killProcessesForForRunReleaseRhodesAppTask() throws InterruptedException
    {
        OSHelper.killProcesses(runningProcessesIdsForRunReleaseRhodesAppTask);
        runningProcessesIdsForRunReleaseRhodesAppTask = new HashSet<Integer>();
    }

    public static void logError(String msg)
    {
        log(IStatus.ERROR, msg);
    }

    public static void logError(String msg, Throwable e)
    {
        log(IStatus.ERROR, msg, e);
    }

    public static void logError(Throwable e)
    {
        log(IStatus.ERROR, "", e);
    }

    public static void logErrorAndThrow(String msg)
    {
        logError(msg);
        throw new ActivatorException(msg);
    }

    public static void logErrorAndThrow(String msg, Throwable e)
    {
        logError(msg, e);
        throw new ActivatorException(msg, e);
    }

    public static void logErrorAndThrow(Throwable e)
    {
        logError(e);
        throw new ActivatorException(e);
    }

    // for severity use IStatus.OK, IStatus.INFO, IStatus.WARNING,
    // IStatus.ERROR, IStatus.CANCEL
    private static void log(int severity, String msg)
    {
        plugin.getLog().log(new Status(severity, PLUGIN_ID, msg));
    }

    // for severity use IStatus.OK, IStatus.INFO, IStatus.WARNING,
    // IStatus.ERROR, IStatus.CANCEL
    private static void log(int severity, String msg, Throwable e)
    {
        plugin.getLog().log(new Status(severity, PLUGIN_ID, msg, e));
    }
}
