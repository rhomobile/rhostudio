package rhogenwizard.sdk.task.rhohub;

import org.json.JSONArray;
import org.json.JSONException;

import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.sdk.task.RubyCodeExecTask;

public class AppListTask extends RubyCodeExecTask
{
    public AppListTask(IRhoHubSetting setting)
    {
        super("require 'rhohub'",
        	  "require 'rest_client'",
        	  "RestClient.proxy = \"" + setting.getHttpProxy() + "\"",
              "Rhohub.token = \"" + setting.getToken() + "\"", 
              "Rhohub.url = \"" + setting.getServerUrl() + "\"", 
              "puts Rhohub::App.list()");
    }

    public JSONArray getOutputAsJSON() throws JSONException
    {
        String listOfApps = this.getOutput();
        
        listOfApps = listOfApps.replaceAll("\\p{Cntrl}", " ");
               
        return new JSONArray(listOfApps);
    }
}
