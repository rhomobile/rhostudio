package rhogenwizard.sdk.task;

import junit.framework.TestCase;

import org.junit.Test;

import rhogenwizard.OSHelper;

public class RubyCodeExecTaskTest extends TestCase
{
    private static String nl = System.getProperty("line.separator");

    @Test
    public void testHelloWorld()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts \"Hello, World!\"");
        task.run();
        check(true, 0, "", "Hello, World!\n", task);
    }

    @Test
    public void testArithmetics()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts 1 + 2");
        task.run();
        check(true, 0, "", "3\n", task);
    }

    @Test
    public void testSeveralStatementsInline()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("a = 4; b = 5; puts a + b");
        task.run();
        check(true, 0, "", "9\n", task);
    }

    @Test
    public void testSeveralStatements()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("h = 'Hello'", "w = 'World'", "puts h + ', ' + w + '!'");
        task.run();
        check(true, 0, "", "Hello, World!\n", task);
    }

    @Test
    public void testSingleQuotes()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts 'The \\'a\\' value is'");
        task.run();
        check(true, 0, "", "The 'a' value is\n", task);
    }

    @Test
    public void testMultyLineCode()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("a = 4\nb = 5\nputs a + b");
        try
        {
            task.run();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(OSHelper.isWindows());
            return;
        }
        assertFalse(OSHelper.isWindows());
        check(true, 0, "", "9\n", task);
    }

    @Test
    public void testUndefinedError()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("a");
        task.run();

        check(false, 1,
            "-e:1:in `<main>': undefined local variable or method `a' for main:Object (NameError)\n", "",
            task);
    }

    @Test
    public void testSyntaxError()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("def");
        task.run();
        check(false, 1, "-e:1: syntax error, unexpected $end\n", "", task);
    }

    @Test
    public void testPartialSuccess()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts 'The \\'a\\' value is'", "puts a");
        task.run();

        check(false, 1,
            "-e:2:in `<main>': undefined local variable or method `a' for main:Object (NameError)\n",
            "The 'a' value is\n", task);
    }

    @Test
    public void testPartialSuccessForMultyLineCode()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts 'The \\'a\\' value is'\nputs a");

        try
        {
            task.run();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(OSHelper.isWindows());
            return;
        }
        assertFalse(OSHelper.isWindows());
        check(false, 1,
            "-e:2:in `<main>': undefined local variable or method `a' for main:Object (NameError)\n",
            "The 'a' value is\n", task);
    }

    private static void check(boolean ok, int exitValue, String error, String output, RubyExecTask task)
    {
        assertEquals(ok, task.isOk());
        assertEquals(exitValue, task.getExitValue());
        assertEquals(error.replaceAll("\n", nl), task.getError());
        assertEquals(output.replaceAll("\n", nl), task.getOutput());
    }
}
