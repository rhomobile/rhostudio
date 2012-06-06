package rhogenwizard.sdk.task;

import junit.framework.TestCase;

import org.junit.Test;

public class RubyCodeExecTaskTest extends TestCase
{
    @Test
    public void testHelloWorld()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts \"Hello, World!\"");
        task.run();
        assertTrue(task.isOk());
        assertEquals(0, task.getExitValue());
        assertEquals("", task.getError());
        assertEquals("Hello, World!\r\n", task.getOutput());
    }

    @Test
    public void testArithmetics()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts 1 + 2");
        task.run();
        assertTrue(task.isOk());
        assertEquals(0, task.getExitValue());
        assertEquals("", task.getError());
        assertEquals("3\n", task.getOutput());
    }

    @Test
    public void testSeveralStatementsInline()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("a = 4; b = 5; puts a + b");
        task.run();
        assertTrue(task.isOk());
        assertEquals(0, task.getExitValue());
        assertEquals("", task.getError());
        assertEquals("9\n", task.getOutput());
    }

    @Test
    public void testSeveralStatements()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("h = 'Hello'", "w = 'World'", "puts h + ', ' + w + '!'");
        task.run();
        assertTrue(task.isOk());
        assertEquals(0, task.getExitValue());
        assertEquals("", task.getError());
        assertEquals("Hello, World!\n", task.getOutput());
    }

    @Test
    public void testUndefinedError()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("a");
        task.run();
        assertFalse(task.isOk());
        assertEquals(1, task.getExitValue());
        assertEquals("-e:1: undefined local variable or method `a' for main:Object (NameError)\n",
            task.getError());
        assertEquals("", task.getOutput());
    }

    @Test
    public void testSyntaxError()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("def");
        task.run();
        assertFalse(task.isOk());
        assertEquals(1, task.getExitValue());
        assertEquals("-e:1: syntax error, unexpected $end\n", task.getError());
        assertEquals("", task.getOutput());
    }

    @Test
    public void testPartialSuccess()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts 'The \\'a\\' value is'", "puts a");
        task.run();
        assertFalse(task.isOk());
        assertEquals(1, task.getExitValue());
        assertEquals("-e:2: undefined local variable or method `a' for main:Object (NameError)\n",
            task.getError());
        assertEquals("The 'a' value is\n", task.getOutput());
    }
    
    @Test
    public void testMultyLineCode()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("puts 'The \\'a\\' value is'\nputs a");
        task.run();
        assertFalse(task.isOk());
        assertEquals(1, task.getExitValue());
        assertEquals("-e:2: undefined local variable or method `a' for main:Object (NameError)\n",
            task.getError());
        assertEquals("The 'a' value is\n", task.getOutput());
    }
}
