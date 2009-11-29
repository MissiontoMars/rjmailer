package com.voxbiblia.rjmailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Instances of this class carries out an SMTP conversation with a specific
 * server, sending a single email message to one or more recipients.
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
class SMTPConversation
    implements Conversation
{
    private static class SMTPException extends Exception
    {
        private String serverLine;
        private int code;
        private boolean hard;

        public SMTPException(String serverLine, int code, boolean hard) {
            this.serverLine = serverLine;
            this.code = code;
            this.hard = hard;
        }

        public String getServerLine() {
            return serverLine;
        }

        public int getCode()
        {
            return code;
        }

        public boolean isHard()
        {
            return hard;
        }
    }


    private static final Logger log = LoggerFactory.getLogger(SMTPConversation.class);

    private static final byte[] EOL = {(byte)'\r', (byte)'\n'};
    private String server;
    private String ehloHostname;
    private FieldGenerator fieldGenerator;
    private SocketFactory socketFactory;
    private InputStream is;
    private OutputStream os;
    private Socket socket;
    private int smtpPort = 25;
    private Set<String> ehloKeywords;
    byte[] inBuf = new byte[1000];

    /**
     * Constructs a ConverstaionHandler that can carry out SMTP conversations
     * to remote SMTP servers. This instance uses the given ehloHostname to
     * identify itself to the remote server.
     *
     * @param ehloHostname used to itentify this ConversationHandler to the
     * remote server during the Client initiation phase (see RFC2821 3.2)
     * @param server the name of the server to connect to
     */
    SMTPConversation(String ehloHostname, String server)
    {
        this.ehloHostname = ehloHostname;
        this.server = server;
    }

    /**
     * Sends an email message to a specified server using the SMTP protocol.
     *
     * @param msg the message to send
     * @param to array of strings specifying recieving email addresses
     * @param ss the SendState to callback the result to
     */
    public void sendMail(RJMMessage msg, List<String> to, SendState ss)
    {
        try {
            doSendMail(msg, to, ss);
        } catch (IOException e) {
            for (String s : to) {
                RJMException rje = new RJMException(ExactCause.IO_EXCEPTION,
                        e.getMessage()).setEmail(s).setServer(server);
                ss.softFailure(s, rje);
            }
        } finally {
            safeClose();
        }
    }

    private void doSendMail(RJMMessage msg, List<String> to, SendState ss)
            throws IOException
    {
        List<String> l = new ArrayList<String>(to);
        checkMessage(msg, to);
        try {
            setupSocket();
            checkStatus(220);
            sendCommand("EHLO " + ehloHostname);
            ehloKeywords = parseEhloResponse(250);

            sendCommand("MAIL FROM: <" + AddressUtil.getAddress(msg.getFrom()) + ">");
            checkStatus(250);
        } catch (Exception e) {
            for (String s : l) {
                convertFailure(e, s, ss);
            }
            return;
        }
        Iterator<String> i = l.iterator();
        while (i.hasNext()) {
            String s = i.next();
            try {
                sendCommand("RCPT TO: <" + s +">");
                checkStatus(250);
            } catch (SMTPException e) {
                convertFailure(e, s, ss);
                i.remove();
            }
        }
        if (l.isEmpty()) {
            return;
        }

        try {
            sendCommand("DATA");
            checkStatus(354);
            writeMessage(msg);

            String result = checkStatus(250).substring("250 ".length());
            for (String s : l) {
                ss.success(s, server, result);
            }
        } catch (SMTPException e) {
            for (String s : l) {
                convertFailure(e, s, ss);
            }
        }
    }

    private void convertFailure(Exception e, String to, SendState ss)
    {

        RJMException rje = null;
        if (e instanceof SMTPException) {
            SMTPException se = (SMTPException)e;
            ExactCause ec = ExactCause.SMTP_UNEXPECTED_STATUS;
            if (se.getCode() == 550) {
                ec = ExactCause.MAILBOX_UNAVAILABLE;
            }
            String serverLine = se.getServerLine();
            if (serverLine.length() > 4) {
                serverLine = serverLine.substring(4);
            }

            rje = new RJMException(ec, serverLine).setEmail(to);
            rje.setStatus(se.getCode()).setServer(server);
            rje.setServerLine(serverLine);
            if (se.isHard()) {
                ss.hardFailure(to, rje);
                return;
            }
        } else if (e instanceof IOException) {
            rje = new RJMException(ExactCause.IO_EXCEPTION,
                    String.format("Failed to communicate with %s:%d: %s",
                            server, smtpPort, e.getMessage()));
            rje.setServer(server).setEmail(to);

        } else if (e instanceof RJMException) {
            rje = (RJMException)e;
        }


        if (rje == null) {
            throw new Error(String.format("unhandled exception %s: %s",
                    e.getClass().getName(), e.getMessage()));
        }
        ss.softFailure(to, rje);
    }

    private void setupSocket() throws SMTPException, IOException
    {
        if (is != null) {
            throw new Error("An SMTPConversation can only be set up once");
        }
        if (fieldGenerator == null) {
            this.fieldGenerator = new FieldGenerator(ehloHostname);
        }
        if (socketFactory == null) {
            socketFactory = new TCPSocketFactory();
        }
        try {
            socket = socketFactory.createSocket(server, smtpPort);
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (UnknownHostException e) {
            throw new RJMException(ExactCause.DOMAIN_INVALID,
                    "Could not resolve server hostname " + server);
        }
    }

    private void checkMessage(RJMMessage msg, List<String> to)
    {
        if (msg == null) {
            throw new RJMInputException("Can not send null message");
        }
        if (to == null || to.isEmpty()) {
            throw new RJMInputException("Not enough addresses to send email " +
                    "to, please supply at least one");
        }

        String from = msg.getFrom();
        if (from == null || from.length() < 1) {
            throw new RJMInputException("Can not send email with null sender " +
                    "address");
        }

    }

    private void safeClose()
    {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log.warn("Closing the of the socket to server '{}' failed " +
                    "with an IOException. Ignoring", server);
        }
    }

    private void writeMessage(RJMMessage msg)
            throws IOException
    {
        os.write(toBytes(AddressUtil.encodeAddressHeader("From", msg.getFrom())));
        List<String> to = msg.getTo();
        if (to != null) {
            os.write(toBytes(AddressUtil.encodeAddressHeader("To", to)));            
        }

        String subject = msg.getSubject();
        if (subject != null) {
            os.write(toBytes(TextEncoder.encodeHeader("Subject",
                    msg.getSubject())));
        }
        os.write(toBytes("Date: " + fieldGenerator.getDate() + "\r\n"));
        os.write(toBytes("Message-ID: <" + fieldGenerator.getMessageId() +
                ">\r\n"));
        String charset = TextEncoder.getCharset(msg.getText());
        os.write(toBytes("Mime-Version: 1.0\r\n"));
        os.write(toBytes("X-Mailer: rjmailer (" + getVersion() + ")\r\n"));
        if (!charset.equals("US-ASCII")) {
            os.write(toBytes("Content-Type: text/plain; charset=" + charset
                    +"\r\n"));

        }
        os.write(toBytes("Content-Transfer-Encoding: quoted-printable\r\n"));

        String data = TextEncoder.canonicalize(msg.getText());
        os.write(toBytes("\r\n" + TextEncoder.encodeQP(data, charset)
                + "\r\n.\r\n"));

    }

    private static String version;

    static String getVersion()
    {
        if (version == null) {
            ClassLoader cl = SMTPConversation.class.getClassLoader();

            InputStream is  = cl.getResourceAsStream("com/voxbiblia/rjmailer/version.properties");
            if (is == null) {
                throw new Error("Could not find properties steam in classpath: " + cl);
            }


            Properties p = new Properties();
            try {
                p.load(is);
            } catch (IOException e) {
                throw new Error("could not load version from jar");
            }
            version = p.getProperty("rjmailer.version");
        }
        return version;
    }

    private static byte[] toBytes(String s)
    {
        try {
            return s.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new Error("no longer recognising US-ASCII");
        }
    }

    /**
     * Reads one ore more response lines from the server and checks the first
     * three chars returned interpreted as digits against the expected status
     * code. Throws an RJMException if a mismatch is found.
     *
     * @param expected the integer value of the thre first ascii chars
     * @throws RJMException if there is a status code mismatch
     * @return the last line returned from server
     * @throws SMTPException if the returned numeric status value doesn't
     * match expected
     * @throws IOException if the io fails
     */
    Set<String> parseEhloResponse(int expected)
            throws IOException, SMTPException
    {
        String line = getServerLine(is, inBuf);
        int status = getStatus(line);
        if (status != expected) {
            boolean hard = true;
            if (status > 399 && status < 500) {
                hard = false;
            }
            throw new SMTPException(line, status, hard);
        }
        Set<String> lines = new HashSet<String>();
        while (line.length() > 3 && line.charAt(3) == '-') {
            line = getServerLine(is, inBuf);
            lines.add(line.substring(4));
        }
        return lines;
    }


    /**
     * Reads one ore more response lines from the server and checks the first
     * three chars returned interpreted as digits against the expected status
     * code. Throws an RJMException if a mismatch is found.
     *
     * @param expected the integer value of the thre first ascii chars
     * @throws RJMException if there is a status code mismatch
     * @return the last line returned from server
     * @throws SMTPException if the returned numeric status value doesn't
     * match expected
     * @throws IOException if the io fails
     */
    String checkStatus(int expected)
            throws IOException, SMTPException
    {
        String line = getServerLine(is, inBuf);
        int status = getStatus(line);
        if (status != expected) {
            boolean hard = true;
            if (status > 399 && status < 500) {
                hard = false;
            }
            throw new SMTPException(line, status, hard);
        }
        while (line.length() > 3 && line.charAt(3) == '-') {
            line = getServerLine(is, inBuf);
        }
        return line;
    }

    void sendCommand(String cmd)
            throws IOException
    {
            os.write(cmd.getBytes("US-ASCII"));
            os.write(EOL);
    }

    static String getServerLine(InputStream is, byte[] inBuf)
            throws IOException
    {
        int i = 0;
        int b = is.read();
        int eolCount = 0;
        while (true) {
            if (b == -1) {
                // TODO: fix
                throw new Error("end of stream from server");
            } else if (eolCount == 0 && b == '\r') {
                eolCount++;
            } else if (eolCount == 1 && b == '\n') {
                break;
            } else {
                inBuf[i++] = (byte)b;
            }

            b = is.read();
        }
        return new String(inBuf, 0, i);
    }

    static int getStatus(String s) {
        return Integer.parseInt(s.substring(0,3));
    }

    // for testing purposes
    void setFieldGenerator(FieldGenerator fieldGenerator)
    {
        this.fieldGenerator = fieldGenerator;
    }

    void setSocketFactory(SocketFactory socketFactory)
    {
        this.socketFactory = socketFactory;
    }

    void setSmtpPort(int smtpPort)
    {
        this.smtpPort = smtpPort;
    }
}
