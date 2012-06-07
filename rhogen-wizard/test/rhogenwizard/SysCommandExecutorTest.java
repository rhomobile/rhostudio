package rhogenwizard;

import static org.junit.Assert.assertEquals;

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
    public void testSingleQuotes() throws IOException, InterruptedException
    {
        runTest("Hello, World!\n", "", "ruby", "-e", "puts 'Hello, World!'");
    }

    @Test
    public void testBackslash() throws IOException, InterruptedException
    {
        runTest("a\\b\n", "", "ruby", "-e", "puts 'a\\b'");
    }

    @Test
    public void testDoubleQuotes() throws IOException, InterruptedException
    {
        runTest("Hello, World!\n", "", "ruby", "-e", "puts \"Hello, World!\"");
    }

    @Test
    public void testBackslashAndDoubleQuote() throws IOException, InterruptedException
    {
        runTest("a\\\"b\n", "", "ruby", "-e", "puts 'a\\\"b'");
    }

    private void runTest(String output, String error, String... commandLine) throws IOException,
        InterruptedException
    {
        assertEquals(0, m_executor.runCommand(Arrays.asList(commandLine)));
        assertEquals(output.replaceAll("\n", nl), m_executor.getCommandOutput());
        assertEquals(error.replaceAll("\n", nl), m_executor.getCommandError());
    }
}
