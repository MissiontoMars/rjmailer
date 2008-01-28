package com.voxbiblia.rjmailer;
/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Ristretto Mail API.
 *
 * The Initial Developers of the Original Code are
 * Timo Stich and Frederik Dietz.
 * Portions created by the Initial Developers are Copyright (C) 2004
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;

import org.columba.ristretto.auth.AuthenticationException;
import org.columba.ristretto.auth.AuthenticationFactory;
import org.columba.ristretto.auth.NoSuchAuthenticationException;
import org.columba.ristretto.composer.MimeTreeRenderer;
import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.io.FileSource;
import org.columba.ristretto.log.RistrettoLogger;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.message.BasicHeader;
import org.columba.ristretto.message.Header;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimeHeader;
import org.columba.ristretto.message.MimeType;
import org.columba.ristretto.parser.AddressParser;
import org.columba.ristretto.parser.ParserException;
import org.columba.ristretto.smtp.SMTPException;
import org.columba.ristretto.smtp.SMTPProtocol;

/**
 * This is a simple command line smtp client. It shows the usage of the
 * Ristretto SMTP capabilities.
 */
public class SimpleSMTP {

    private static final String helpMessage =
        "Usage : SimpleSMTP smtp-server from-address to-address subject [optional args]\n\n"
            + "Example: SimpleSMTP smtp.test.com sender@test.com example@test.com \"This is a test message\" --text \"Hello, World!\"\n\n"
            + "--body\t\tMessage String\n"
            + "--textfile\tMessage from a text file\n"
            + "--attachment\tFile Attachment\n"
            + "--auth\t\tusername password\n"
            + "--help\t\tShow this help screen\n";

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 4 || args[0].equals("--help")) {
            System.out.println(helpMessage);
            return;
        }

        String smtpServer = args[0];

        // Parse and check the given to- and from-address
        Address fromAddress;
        try {
            fromAddress = AddressParser.parseAddress(args[1]);
        } catch (ParserException e) {
            System.err.println("Invalid from-address : " + e.getSource());
            return;
        }

        Address toAddress;
        try {
            toAddress = AddressParser.parseAddress(args[2]);
        } catch (ParserException e) {
            System.err.println("Invalid to-address : " + e.getSource());
            return;
        }

        String subject = args[3];

        String body = "Hello, World!";
        String textfile = null;
        String attachment = null;
        String user = null;
        String password = null;
        boolean verbose = false;

        // Initialize optional arguments

        for (int i = 4; i < args.length;) {
            if (args[i].equals("--body")) {
                body = args[i + 1];
                i += 2;
            } else if (args[i].equals("--textfile")) {
                textfile = args[i + 1];
                i += 2;
            } else if (args[i].equals("--attachment")) {
                attachment = args[i + 1];
                i += 2;
            } else if (args[i].equals("--auth")) {
                user = args[i + 1];
                password = args[i + 2];
                i += 3;
            } else if (args[i].equals("--verbose")) {
                verbose = true;
                i++;
            }
        }

        // If verbose log IO to System.out
        // Note: This might also be e.g. a filestream to log to a file
        if (verbose) {
            RistrettoLogger.setLogStream(System.out);
        }

        // First we compose the message and then we connect to the SMTP
        // server to send it.

        // PART 1 : Composing a message

        // Header is the actual header while BasicHeader wraps
        // a Header object to give easy access to the Header.
        Header header = new Header();
        BasicHeader basicHeader = new BasicHeader(header);

        // Add the fields to the header
        // Note that the basicHeader is only a convienience wrapper
        // for our header object.
        basicHeader.setFrom(fromAddress);
        basicHeader.setTo(new Address[] { toAddress });
        basicHeader.setSubject(subject, Charset.forName("ISO-8859-1"));
        basicHeader.set("X-Mailer", "SimpleSMTP example / Ristretto API");

        // Now a mimepart is prepared which actually holds the message
        // The mimeHeader is another convienice wrapper for the header
        // object
        MimeHeader mimeHeader = new MimeHeader(header);
        mimeHeader.set("Mime-Version", "1.0");
        LocalMimePart root = new LocalMimePart(mimeHeader);

        LocalMimePart textPart;

        // If we have an attachment we must compose a multipart message
        if (attachment == null) {
            textPart = root;
        } else {
            mimeHeader.setMimeType(new MimeType("multipart", "mixed"));

            textPart = new LocalMimePart(new MimeHeader());
            root.addChild(textPart);
        }

        // Now we can add some message text
        MimeHeader textHeader = textPart.getHeader();
        textHeader.setMimeType(new MimeType("text", "plain"));
        if (body != null) {

            // a simple string
            root.setBody(new CharSequenceSource(body));
        } else if (textfile != null) {

            // or a text file
            try {
                root.setBody(new FileSource(new File(textfile)));
            } catch (IOException e1) {
                System.err.println(e1.getLocalizedMessage());
                return;
            }
        }


        // Now we compose the attachment
        if (attachment != null) {
            MimeHeader attachmentHeader =
                new MimeHeader("application", "octet-stream");
            attachmentHeader.setContentTransferEncoding("base64");
            attachmentHeader.putDispositionParameter("filename", attachment);

            LocalMimePart attachmentPart = new LocalMimePart(attachmentHeader);
            try {
                attachmentPart.setBody(new FileSource(new File(attachment)));
            } catch (IOException e1) {
                System.err.println(e1.getLocalizedMessage());
                return;
            }
            root.addChild(attachmentPart);
        }

        InputStream messageSource;
        try {
            // Finally we render the message to an inputstream
            messageSource = MimeTreeRenderer.getInstance().renderMimePart( root );
        } catch (Exception e2) {
            System.err.println(e2.getLocalizedMessage());
            return;
        }


        // Part 2 : Sending the message

        //Construct the proctol that is bound to the SMTP server
        SMTPProtocol protocol = new SMTPProtocol(smtpServer);
        try {
            // Open the port
            protocol.openPort();

            // The EHLO command gives us the capabilities of the server
            String capabilities[] = protocol.ehlo(InetAddress.getLocalHost());

            // Authenticate
            if(user != null ) {
                String authCapability = null;
                for( int i=0; i<capabilities.length; i++) {
                    if( capabilities[i].startsWith("AUTH")) {
                        authCapability = capabilities[i];
                        break;
                    }
                }
                if( authCapability != null ) {
                    try {
                        protocol.auth( AuthenticationFactory.getInstance().getSecurestMethod( authCapability ), user, password.toCharArray() );
                    } catch (NoSuchAuthenticationException e3) {
                        System.err.println(e3.getLocalizedMessage());
                        return;
                    } catch ( AuthenticationException e ) {
                        System.err.println(e.getMessage());
                        return;

                    }
                } else {
                    System.err.println("Server does not support Authentication!");
                    return;
                }

            }

            // Setup from and recipient
            protocol.mail(fromAddress);
            protocol.rcpt(toAddress);

            // Finally send the data
            protocol.data(messageSource);

            // And close the session
            protocol.quit();

        } catch (IOException e1) {
            System.err.println(e1.getLocalizedMessage());
            return;
        } catch (SMTPException e1) {
            System.err.println(e1.getMessage());
            return;
        }

		System.exit(0);
    }
}
