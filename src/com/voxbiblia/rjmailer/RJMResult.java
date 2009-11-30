package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Instances of this class contains information about the sending of a message.
 */
public class RJMResult
    implements SendResult
{

    public enum Status {
        SENT, DEFERRED
    }

    private String recievingServer;
    private String result;
    private Status status;
    private List<RJMException> softFailures;
    private String tlsCipherSuite;
    private String tlsCertHash;

    /**
     * Construtcts an RJMResult instance.
     *
     * @param recievingServer The server that the message was sent to
     * @param result The line recieved from the server containing tracking information
     * @param status the status of this send operation
     */
    public RJMResult(String recievingServer, String result, Status status)
    {
        this.recievingServer = recievingServer;
        this.result = result;
        this.status = status;

    }

    /**
     * The hostname of the server receiving this message
     *
     * @return the hostname of the receving server
     */
    public String getRecievingServer()
    {
        return recievingServer;
    }

    public void setRecievingServer(String recievingServer)
    {
        this.recievingServer = recievingServer;
    }

    /**
     * Returns the string replied by the mail server when sending the mail
     * message body using the DATA smtp command.
     *
     * @return the server reply to a successful delivery
     */
    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    /**
     * If there were temporary delivery errors when trying to send mail via
     * all the incoming mail servers for this recipient this method can
     * return Status.DEFERRED, meaning that the message was put in a queue on
     * an outgoing mail server waiting to be delivered. If the message
     * was successfully sent, Status.SENT is returned.
     *
     * @return Status.SENT or Status.DEFERRED
     */
    public Status getStatus()
    {
        return status;
    }


    /**
     * If there were soft failures when trying to send this message, for example
     * a timeout when attempting to deliver to the primary incoming server for
     * a given recipient those errors are returned by calling this method.
     *
     * @return a list of soft errors
     */
    public List<RJMException> getSoftFailures()
    {
        return softFailures;
    }

    public void setSoftFailures(List<RJMException> softFailures)
    {
        this.softFailures = softFailures;
    }

    /**
     * If the connection to the mail server sending this message was protected
     * using the STARTTLS mechanism, this method returns a cipher suite string
     * indicating what set of cryptographic algorithms were used when setting
     * up the encrypted connection.
     *
     * @return the cipher suite identifier
     */
    public String getTlsCipherSuite()
    {
        return tlsCipherSuite;
    }

    public void setTlsCipherSuite(String tlsCipherSuite)
    {
        this.tlsCipherSuite = tlsCipherSuite;
    }

    /**
     * If the connection to the mail server sending this message was protected
     * using the STARTTLS mechanism, this method returns the SHA-1 cryptographic
     * checksum of the TLS server certificate. This can be compared with the
     * certificate checksum extracted via the command below to detect man in the
     * middle attacks.
     *
     * <tt>openssl x509 -fingerprint -sha1 -noout -in cert.crt</tt>
     *
     * @return the server certificate fingerprint.
     */
    public String getTlsCertHash()
    {
        return tlsCertHash;
    }

    public void setTlsCertHash(String tlsCertHash)
    {
        this.tlsCertHash = tlsCertHash;
    }
}
