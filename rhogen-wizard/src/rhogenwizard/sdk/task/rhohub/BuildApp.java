package rhogenwizard.sdk.task.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

import rhogenwizard.rhohub.IRemoteProjectDesc;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.sdk.task.RubyCodeExecTask;

public class BuildApp extends RubyCodeExecTask
{
    private static class BuildAppArgsHelper
    {
        IRemoteProjectDesc m_project = null;
        JSONObject         m_buildObj = null;
        
        public BuildAppArgsHelper(IRemoteProjectDesc project, IRhoHubSetting setting)
        {
            try
            {
                m_project = project;
                
                JSONObject buildOptions = new JSONObject();        
                buildOptions.put("target_device", setting.getSelectedPlatform());
                buildOptions.put("version_tag", setting.getAppBranch());
                buildOptions.put("rhodes_version", setting.getRhodesBranch());
                
                m_buildObj = new JSONObject();
                m_buildObj.put("build", buildOptions);
            }
            catch (JSONException e)
            {
                m_buildObj = null;
            }                
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            
            try
            {
                sb.append("puts Rhohub::Build.create(");
                sb.append("{:app_id =>" + m_project.getId() + "},");
                sb.append("\"" + m_buildObj.toString().replaceAll("\\\"", "\\\\\"") + "\")");
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
        return new JSONObject(super.getOutput());
    }

    public BuildApp(IRemoteProjectDesc project, IRhoHubSetting setting)
    {        
        super("require 'rhohub'", 
              "Rhohub.token = \"" + setting.getToken() + "\"", 
              "Rhohub.url = \"" + setting.getServerUrl() + "\"", 
              new BuildAppArgsHelper(project, setting).toString());
    }
}