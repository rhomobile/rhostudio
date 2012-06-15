package rhogenwizard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpDownloadTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void downloadSomething() throws MalformedURLException, InterruptedException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpDownload hd = new HttpDownload(new URL(
            "http://sourceforge.net/projects/nsis/files/NSIS%202/2.46/nsis-2.46-strlen_8192.zip/download"),
            os);
        hd.join(0);
        assertTrue(hd.ok());
        assertNull(hd.getException());
        assertEquals(352227, hd.getSize());
        assertEquals(352227, os.size());
    }

    // @Test
    public void downloadSomethingBig() throws MalformedURLException, InterruptedException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpDownload hd = new HttpDownload(new URL(
            "http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/technology/epp/downloads/release/helios/SR2/eclipse-rcp-helios-SR2-win32.zip"),
            os);
        hd.join(0);
        assertTrue(hd.ok());
        assertNull(hd.getException());
        assertEquals(352227, os.size());
    }
}
