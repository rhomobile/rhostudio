package rhogenwizard.sdk.task.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.sdk.task.RubyCodeExecTask;

public class CreateAppTask extends RubyCodeExecTask
{
    public CreateAppTask(IRhoHubSetting setting, String appName)
    {
        super("require 'rhohub'",
          	  "require 'rest_client'",
          	  "RestClient.proxy = \"" + setting.getHttpProxy() + "\"",
              "Rhohub.token = \"" + setting.getToken() + "\"", 
              "Rhohub.url = \"" + setting.getServerUrl() + "\"", 
              "puts Rhohub::App.create({:app => {:name => \'" + appName + "\', :app_type => \'rhodes\'}})");
    }

    public JSONObject getOutputAsJSON() throws JSONException
    {
        String listOfApps = this.getOutput();
        
        listOfApps = listOfApps.replaceAll("\\p{Cntrl}", " ");
               
        return new JSONObject(listOfApps);
    }
}
