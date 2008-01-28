package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.voxbiblia.rjmailer.RJMMailMessage;
//import com.voxbiblia.rjmailer.samples.SyncMailSender;

/**
 * Created by IntelliJ IDEA.
 * User: noa
 * Date: Feb 17, 2007
 * Time: 9:23:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailSenderTest
    extends TestCase
{
	public void testDummy()
	{
		// empty
	}
/*
    public void testSendMail()
    {
        SyncMailSender ms = new SyncMailSender();
        ms.setServer("johanna.resare.com");
        RJMMailMessage mm = new RJMMailMessage();
        mm.setFrom("noa@resare.com");
        mm.setTo("noa@resare.com");
        ms.send(mm);
    }


    public void testMakeStream()
    {
        RJMMailMessage jmm = new RJMMailMessage();
        jmm.setFrom("Noa Resare <noa@resare.com>");
        jmm.setTo("Noa Resare <noa@voxbiblia.se>");
        jmm.setSubject("Simple subject");
        jmm.setText("some text");

        InputStream is = SyncMailSender.makeStream(jmm);
        
        byte[] buf = new byte[8192];
        try {
            int bytesRead = is.read(buf);
            while(bytesRead != -1) {
                System.out.write(buf, 0, bytesRead);
                bytesRead = is.read(buf);
            }
            System.out.flush();
        } catch (IOException e) {
            throw new Error(e);
        }

    }



    public void testDetermineCharset()
    {
        RJMMailMessage jmm = new RJMMailMessage();
        jmm.setText("only ascii");
        assertEquals(Charset.forName("US-ASCII"), SyncMailSender.determineCharset(jmm));
        jmm.setText("Lådbilsrace");
        assertEquals(Charset.forName("ISO-8859-1"), SyncMailSender.determineCharset(jmm));
        jmm.setText("Currency sign for euro: €");
        assertEquals(Charset.forName("UTF-8"), SyncMailSender.determineCharset(jmm));
    }
*/
}
