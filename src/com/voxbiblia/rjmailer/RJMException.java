package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * The root exception of the RJMailer exception hierarchy.
 */
public class RJMException
    extends RuntimeException
    implements SendResult
{

    private final ExactCause exactCause;
    private String domain;
    private String email;
    private String server;
    private String serverLine;
    private int status;
    private List<RJMException> softFailures;


    /**
	 * Used to shut up eclipse build warnings
	 */
	private static final long serialVersionUID = 9459589695294L;

    protected RJMException(ExactCause exactCause, String message)
    {
        super(message);
        this.exactCause = exactCause;
    }

    public int getStatus()
    {
        return status;
    }

    public List<RJMException> getSoftFailures()
    {
        return softFailures;
    }

    public RJMException setSoftFailures(List<RJMException> setSoftFailures)
    {
        this.softFailures = setSoftFailures;
        return this;
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

    public RJMException setStatus(int status)
    {
        this.status = status;
        return this;
    }

}
