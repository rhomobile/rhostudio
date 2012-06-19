package rhogenwizard.sdk.task.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.sdk.task.RubyCodeExecTask;


public class ShowBuildTask extends RubyCodeExecTask
{
    public ShowBuildTask(IRhoHubSetting setting, Integer appId, Integer buildId)
    {
        super("require 'rhohub'", 
              "Rhohub.token = \"" + setting.getToken() + "\"", 
              "Rhohub.url = \"" + setting.getServerUrl() + "\"", 
              "puts Rhohub::Build.show({ :app_id => " + appId.toString() + ", :id =>" + buildId.toString() +  "})");
    }

    public JSONObject getOutputAsJSON() throws JSONException
    {
        return new JSONObject(super.getOutput());
    }
}
