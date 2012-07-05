package rhogenwizard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class SysCommandExecutorTest
{
    private static String      nl            = System.getProperty("line.separator");
    private static ILogDevice  nullLogDevice = new ILogDevice()
                                             {
                                                 @Override
                                                 public void log(String str)
                                                 {
                                                 }
                                             };

    private SysCommandExecutor m_executor    = null;

    @Before
    public void setUp()
    {
        m_executor = new SysCommandExecutor();
        m_executor.setOutputLogDevice(nullLogDevice);
        m_executor.setErrorLogDevice(nullLogDevice);
    }

    @Test
    public void testLiterals() throws Exception
    {
        String[] literals = { "a b", "a\tb", "a\\b", "a \\'b", " \"\\\\", "a\\\\", "a b\\\\", "a\"b",
            "a\\\"b", "a b\"", "\"a b", "\"a b\"", "\"'acb'\"", "\"a'c'b\"", "a=b", "a>&b", "a()%!^\"<>&|b",
            "'/app/SpecRunner'" };
        for (String literal : literals)
        {
            runRakeTest(literal);
            runRubyTest(literal);
            runCrtTest(literal);
        }
    }

    @Test
    public void testNewLine() throws Exception
    {
        try
        {
            runTest("", "", SysCommandExecutor.CRT, "echo", "a\nb");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(OSHelper.isWindows());
            return;
        }
        assertFalse(OSHelper.isWindows());
    }

    @Test
    public void testSingleQuotes() throws IOException, InterruptedException
    {
        runTest("Hello, World!\n", "", SysCommandExecutor.RUBY, "ruby", "-e", "puts 'Hello, World!'");
    }

    @Test
    public void testBackslash() throws IOException, InterruptedException
    {
        runTest("a\\b\n", "", SysCommandExecutor.RUBY, "ruby", "-e", "puts 'a\\b'");
    }

    @Test
    public void testDoubleQuotes() throws IOException, InterruptedException
    {
        runTest("Hello, World!\n", "", SysCommandExecutor.RUBY, "ruby", "-e", "puts \"Hello, World!\"");
    }

    @Test
    public void testBackslashAndDoubleQuote() throws IOException, InterruptedException
    {
        runTest("a\\\"b\n", "", SysCommandExecutor.RUBY, "ruby", "-e", "puts 'a\\\"b'");
    }

    @Test
    public void testALotOfDoubleQuotes() throws IOException, InterruptedException
    {
        runTest("\"\"\"\"\"\"\"\"\n", "", SysCommandExecutor.RUBY, "ruby", "-e",
            "puts \"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\"");
    }

    @Test
    public void testSingleWordCommand() throws IOException, InterruptedException
    {
        String out = (OSHelper.isWindows()) ? "ECHO is on.\n" : "\n";
        runTest(out, "", SysCommandExecutor.CRT, "echo");
    }

    private void runTest(String output, String error, SysCommandExecutor.Decorator decorator,
        String... commandLine) throws IOException, InterruptedException
    {
        assertEquals(0, m_executor.runCommand(decorator, Arrays.asList(commandLine)));
        assertEquals(output.replaceAll("\n", nl), m_executor.getCommandOutput());
        assertEquals(error.replaceAll("\n", nl), m_executor.getCommandError());
    }

    private void runCrtTest(String literal) throws Exception
    {
        // System.out.println("test crt  [" + literal + "]");
        runTest(SysCommandExecutor.CRT, "check_pattern", literal, pattern(literal));
    }

    private void runRubyTest(String literal) throws Exception
    {
        // System.out.println("test ruby [" + literal + "]");
        runTest(SysCommandExecutor.RUBY, "ruby", "check_pattern.rb", literal, pattern(literal));
    }

    private void runRakeTest(String literal) throws Exception
    {
        // System.out.println("test rake [" + literal + "]");
        runTest(SysCommandExecutor.RUBY_BAT, "rake", "check_pattern", "literal=" + literal, "pattern="
            + pattern(literal));
    }

    private void runTest(SysCommandExecutor.Decorator decorator, String... args) throws Exception
    {
        String pwd = getJUnitToolsDir();
        m_executor.setWorkingDirectory(pwd);
        try
        {
            assertEquals(0, m_executor.runCommand(decorator, Arrays.asList(args)));
        }
        catch (AssertionError e)
        {
            System.err.println("Error");
            System.err.println("PWD: " + pwd);
            System.err.println("CMD: " + Arrays.toString(args));
            System.err.println("OUT: " + m_executor.getCommandOutput());
            System.err.println("ERR: " + m_executor.getCommandError());
            throw e;
        }
    }

    private static String pattern(String literal)
    {
        StringBuilder sb = new StringBuilder();
        for (char c : literal.toCharArray())
        {
            sb.append(String.format("%02x", (int) c));
        }
        return sb.toString();
    }

    public static String getJUnitToolsDir()
    {
        String rawDir = System.getProperty("rho_junit_tools");
        String message = "Add `-Drho_junit_tools=${project_loc:RhogenWizard}/../tools/junit/bin` flag as VM argument in JRE definition.";

        assertNotNull(message, rawDir);

        String dir = null;
        try
        {
            dir = new File(rawDir).getCanonicalPath();
        }
        catch (IOException e)
        {
            fail("Something wrong with directory '" + rawDir + "'");
        }

        return dir;
    }
}
