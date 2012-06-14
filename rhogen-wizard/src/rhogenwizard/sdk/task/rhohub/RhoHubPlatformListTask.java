package rhogenwizard.sdk.task.rhohub;

import org.json.JSONArray;
import org.json.JSONException;

import rhogenwizard.sdk.task.RubyCodeExecTask;

public class RhoHubPlatformListTask extends RubyCodeExecTask
{
    public RhoHubPlatformListTask(String userToken, String serverUrl)
    {
        super("require 'rhohub'", 
              "Rhohub.token = \"" + userToken + "\"", 
              "Rhohub.url = \"" + serverUrl + "\"", 
              "puts Rhohub::Build.platforms()");
    }

    public JSONArray getOutputAsJSON() throws JSONException
    {
        String listOfApps = this.getOutput();
        
        listOfApps = listOfApps.replaceAll("\\p{Cntrl}", " ");
        
        // TODO - its temporary solutions, wait Lucas for fix api output 
        String[] a = listOfApps.split("\\[");
        
        if (a.length < 2)
            return null;
        
        String[] b = a[1].split("\\]");
        // end of
               
        return new JSONArray("[" + b[0] + "]");
    }
}
