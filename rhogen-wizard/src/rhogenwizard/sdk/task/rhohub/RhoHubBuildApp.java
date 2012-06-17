package rhogenwizard.sdk.task.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

import rhogenwizard.rhohub.IRemoteProjectDesc;
import rhogenwizard.sdk.task.RubyCodeExecTask;

public class RhoHubBuildApp extends RubyCodeExecTask
{
    private static class BuildAppArgsHelper
    {
        IRemoteProjectDesc m_project = null;
        JSONObject         m_buildObj = null;
        
        public BuildAppArgsHelper(IRemoteProjectDesc project, String buildPlatform, String appVersion, String rhodesVersion)
        {
            try
            {
                m_project = project;
                
                JSONObject buildOptions = new JSONObject();        
                buildOptions.put("target_device", buildPlatform);
                buildOptions.put("version_tag", appVersion);
                buildOptions.put("rhodes_version", rhodesVersion);

                //m_buildObj = buildOptions;
                
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
                
//                sb.append("{:build => " + m_buildObj.toString()/*.replaceAll("\\:", "=>")*/ + "}");
//                sb.append(".to_json)");
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

    public RhoHubBuildApp(IRemoteProjectDesc project, String userToken, String serverUrl, String buildPlatform, String appVersion, String rhodesVersion)
    {        
        super("require 'rhohub'", 
              "Rhohub.token = \"" + userToken + "\"", 
              "Rhohub.url = \"" + serverUrl + "\"", 
              new BuildAppArgsHelper(project, buildPlatform, appVersion, rhodesVersion).toString());
    }
}