package rhogenwizard.sdk.task;

public class GenerateRhodesModelTask extends RubyExecTask
{
    public GenerateRhodesModelTask(String workDir, String modelName, String modelFields)
    {
        super(workDir, "rhodes", "model", modelName, prepareModelAttributes(modelFields));
    }

    private static String prepareModelAttributes(String modelAttr)
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
