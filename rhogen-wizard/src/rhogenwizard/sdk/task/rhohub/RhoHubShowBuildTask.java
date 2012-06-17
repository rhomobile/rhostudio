package rhogenwizard.sdk.task.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

import rhogenwizard.sdk.task.RubyCodeExecTask;


public class RhoHubShowBuildTask extends RubyCodeExecTask
{
    public RhoHubShowBuildTask(String userToken, String serverUrl, Integer appId, Integer buildId)
    {
        super("require 'rhohub'", 
              "Rhohub.token = \"" + userToken + "\"", 
              "Rhohub.url = \"" + serverUrl + "\"", 
              "puts Rhohub::Build.show({ :app_id => " + appId.toString() + ", :id =>" + buildId.toString() +  "})");
    }

    public JSONObject getOutputAsJSON() throws JSONException
    {
        return new JSONObject(super.getOutput());
    }
}
