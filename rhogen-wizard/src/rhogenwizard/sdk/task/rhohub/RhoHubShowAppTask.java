package rhogenwizard.sdk.task.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

import rhogenwizard.sdk.task.RubyCodeExecTask;


public class RhoHubShowAppTask extends RubyCodeExecTask
{
    public RhoHubShowAppTask(String userToken, String serverUrl, Integer appId)
    {
        super("require 'rhohub'", 
              "Rhohub.token = \"" + userToken + "\"", 
              "Rhohub.url = \"" + serverUrl + "\"", 
              "puts Rhohub::App.show({ :app_id => " + appId.toString() + "})");
    }

    public JSONObject getOutputAsJSON() throws JSONException
    {
        String output = super.getOutput();
        
        String[] outArray = output.split("\\{");
        
        if (outArray.length < 2)
            return null;
        
        return new JSONObject(outArray[1]);  
    }
}
