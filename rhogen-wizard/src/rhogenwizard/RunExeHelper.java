package rhogenwizard;

import java.io.IOException;
import java.util.Arrays;

import rhogenwizard.constants.CommonConstants;

public class RunExeHelper
{
    private static String run(SysCommandExecutor.Decorator decorator, String... args)
    {
        SysCommandExecutor executor = new SysCommandExecutor();
        try
        {
            executor.runCommand(decorator, Arrays.asList(args));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return "";
        }
        return executor.getCommandOutput();
    }

    public static void killBbSimulator()
    {
        run(SysCommandExecutor.CRT, "taskkill", "/IM", "fledge.exe");
    }

    public static String getSdkInfo()
    {
        String out = run(SysCommandExecutor.RUBY_BAT, "get-rhodes-info", "--rhodes-path");
        return out.replaceAll("\\p{Cntrl}", "");
    }

    public static boolean checkRhodesVersion(String sdkVer)
    {
        String cmdOutput = run(SysCommandExecutor.RUBY_BAT, "get-rhodes-info", "--rhodes-ver=" + sdkVer);
        cmdOutput = cmdOutput.replaceAll("\\p{Cntrl}", "");
        return cmdOutput.equals(CommonConstants.okRhodesVersionFlag);
    }
}
