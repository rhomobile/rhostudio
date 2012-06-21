package rhogenwizard.sdk.task.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.sdk.task.RubyCodeExecTask;

public class CreateAppTask extends RubyCodeExecTask
{
    private static String getAppParams(String appName)
    {
        try
        {
            JSONObject createOptions = new JSONObject();
            createOptions.put("name", appName);
            
            JSONObject wrapObject = new JSONObject();
            wrapObject.put("app", createOptions);
            
            return "\"" + wrapObject.toString().replaceAll("\\\"", "\\\\\"") + "\"";
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }
    
    public CreateAppTask(IRhoHubSetting setting, String appName)
    {
        super("require 'rhohub'", 
              "Rhohub.token = \"" + setting.getToken() + "\"", 
              "Rhohub.url = \"" + setting.getServerUrl() + "\"", 
              "puts Rhohub::App.create(" + getAppParams(appName) + ")");
    }

    public JSONObject getOutputAsJSON() throws JSONException
    {
        String listOfApps = this.getOutput();
        
        listOfApps = listOfApps.replaceAll("\\p{Cntrl}", " ");
               
        return new JSONObject(listOfApps);
    }
}
