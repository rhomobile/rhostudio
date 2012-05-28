package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesModelTask extends RhodesTask
{
    private final String m_workDir;
    private final String m_modelName;
    private final String m_modelFields;

    public GenerateRhodesModelTask(String workDir, String modelName, String modelFields)
    {
        m_workDir = workDir;
        m_modelName = modelName;
        m_modelFields = prepareModelAttributes(modelFields);
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = Arrays.asList(m_rhogenExe, "model", m_modelName, m_modelFields);

        m_taskResult.clear();
        int result = TaskResultConverter.failCode;
        
        try
        {
            m_executor.setWorkingDirectory(m_workDir);
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
