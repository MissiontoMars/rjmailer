package com.voxbiblia.rjmailer;

/**
 * The root exception of the RJMailer exception hierarchy.
 */
public class RJMException
    extends RuntimeException
    implements SendResult
{

    public enum ExactCause
    {
        DOMAIN_NOT_FOUND, DOMAIN_INVALID, DOMAIN_FAILURE,
        SMTP_CONNECT, SMTP_UNEXPECTED_STATUS,
        INVALID_INPUT, ALL_SERVERS_FAILED
    }

    private final ExactCause exactCause;
    private String domain;
    private String email;
    private String server;
    private String serverLine;

    /**
	 * Used to shut up eclipse build warnings
	 */
	private static final long serialVersionUID = 9459589695294L;

    protected RJMException(ExactCause exactCause, String message)
    {
        super(message);
        this.exactCause = exactCause;
    }

    public ExactCause getExactCause()
    {
        return exactCause;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getEmail()
    {
        return email;
    }

    public String getServer()
    {
        return server;
    }

    public String getServerLine()
    {
        return serverLine;
    }

    public RJMException setDomain(String domain)
    {
        this.domain = domain;
        return this;
    }

    public RJMException setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public RJMException setServer(String server)
    {
        this.server = server;
        return this;
    }

    public RJMException setServerLine(String serverLine)
    {
        this.serverLine = serverLine;
        return this;
    }
}
