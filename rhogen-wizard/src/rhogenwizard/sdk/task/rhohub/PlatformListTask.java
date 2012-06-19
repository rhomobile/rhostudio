package rhogenwizard.sdk.task.rhohub;

import org.json.JSONArray;
import org.json.JSONException;

import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.sdk.task.RubyCodeExecTask;

public class PlatformListTask extends RubyCodeExecTask
{
    public PlatformListTask(IRhoHubSetting setting)
    {
        super("require 'rhohub'", 
              "Rhohub.token = \"" + setting.getToken() + "\"", 
              "Rhohub.url = \"" + setting.getServerUrl() + "\"", 
              "puts Rhohub::Build.platforms()");
    }

    public JSONArray getOutputAsJSON() throws JSONException
    {
        String listOfApps = this.getOutput();
        
        listOfApps = listOfApps.replaceAll("\\p{Cntrl}", " ");
               
        return new JSONArray(listOfApps);
    }
}
