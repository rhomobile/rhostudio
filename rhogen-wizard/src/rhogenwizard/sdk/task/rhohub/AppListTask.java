package rhogenwizard.sdk.task.rhohub;

import org.json.JSONArray;
import org.json.JSONException;

import rhogenwizard.sdk.task.RubyCodeExecTask;

public class AppListTask extends RubyCodeExecTask
{
    public AppListTask(String userToken, String serverUrl)
    {
        super("require 'rhohub'", 
              "Rhohub.token = \"" + userToken + "\"", 
              "Rhohub.url = \"" + serverUrl + "\"", 
              "puts Rhohub::App.list()");
    }

    public JSONArray getOutputAsJSON() throws JSONException
    {
        String listOfApps = this.getOutput();
        
        listOfApps = listOfApps.replaceAll("\\p{Cntrl}", " ");
               
        return new JSONArray(listOfApps);
    }
}
