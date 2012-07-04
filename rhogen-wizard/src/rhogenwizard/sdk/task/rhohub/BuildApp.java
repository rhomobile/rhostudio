package rhogenwizard.sdk.task.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.RemoteProjectDesc;
import rhogenwizard.sdk.task.RubyCodeExecTask;

public class BuildApp extends RubyCodeExecTask
{
    private static class BuildAppArgsHelper
    {
        RemoteProjectDesc m_project = null;
        IRhoHubSetting     m_setting = null;
        
        public BuildAppArgsHelper(RemoteProjectDesc project, IRhoHubSetting setting)
        {
            m_project  = project;
            m_setting  = setting;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            
            try
            {
                sb.append("puts Rhohub::Build.create(");
                sb.append("{:app_id =>" + m_project.getId() + "},");
                sb.append("{ :build => {");
                sb.append(" \"target_device\"" + " => " + "\"" + m_setting.getSelectedPlatform() + "\",");
                sb.append(" \"version_tag\"" + " => " + "\"" + m_setting.getAppBranch() + "\",");
                sb.append(" \"rhodes_version\"" + " => " + "\"" + m_setting.getRhodesBranch() + "\"");
                sb.append("}})");
            }
            catch (JSONException e)
            {
                return "";
            }
            
            return sb.toString();
        }        
    }
    
    public JSONObject getOutputAsJSON() throws JSONException
    {
        String listOfApps = this.getOutput();
        
        listOfApps = listOfApps.replaceAll("\\p{Cntrl}", " ");
               
        return new JSONObject(listOfApps);
    }

    public BuildApp(RemoteProjectDesc project, IRhoHubSetting setting)
    {        
        super("require 'rhohub'", 
              "Rhohub.token = \"" + setting.getToken() + "\"", 
              "Rhohub.url = \"" + setting.getServerUrl() + "\"", 
              new BuildAppArgsHelper(project, setting).toString());
    }
}