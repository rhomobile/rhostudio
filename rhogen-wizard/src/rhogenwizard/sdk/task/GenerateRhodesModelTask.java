package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesModelTask extends RhodesTask
{
    public static final String modelName = "model-name";
    public static final String modelFields = "model-fields";

    public GenerateRhodesModelTask()
    {
    }

    public GenerateRhodesModelTask(String projectLocation, String modelName, String modelParams)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(GenerateRhodesModelTask.workDir, projectLocation);
        params.put(GenerateRhodesModelTask.modelName, modelName);
        params.put(GenerateRhodesModelTask.modelFields, modelParams);
        m_taskParams = params;
    }

    @Override
    public void run()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException(
                    "parameters data is invalid [GenerateRhodesModelTask]");

        String workDir = (String) m_taskParams.get(IRunTask.workDir);
        String modelName = (String) m_taskParams.get(GenerateRhodesModelTask.modelName);
        String modelFields = (String) m_taskParams.get(GenerateRhodesModelTask.modelFields);

        modelFields = prepareModelAttributes(modelFields);

        List<String> cmdLine = Arrays.asList(m_rhogenExe, "model", modelName, modelFields);

        // cmdLine = Arrays.asList("sleep", "1000");

        m_taskResult.clear();
        int result = TaskResultConverter.failCode;
        try
        {
            m_executor.setWorkingDirectory(workDir);
            result = m_executor.runCommand(cmdLine);
        }
        catch (Exception e)
        {
        }
        m_taskResult.put(resTag, result);
    }

    private String prepareModelAttributes(String modelAttr)
    {
        String s = modelAttr;
        s = s.replaceAll("^\\s*", ""); // trim leading spaces
        s = s.replaceAll("\\s*$", ""); // trim trailing spaces
        s = s.replaceAll("\\s*,\\s*", ","); // remove spaces around commas
        s = s.replaceAll("[^\\w,]", "_"); // replace all non-word chars with
                                          // underlines preserving commas
        return s;
    }
}
