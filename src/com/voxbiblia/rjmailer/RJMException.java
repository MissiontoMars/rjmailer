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
        SMTP_CONNECT, SMTP_UNEXPECTED_STATUS
    }

    public static class Builder
    {
        private String message;
        private ExactCause exactCause;
        private String email;
        private String domain;
        private String server;
        private String serverLine;

        public Builder setMessage(String message)
        {
            this.message = message;
            return this;
        }

        public Builder setExactCause(ExactCause exactCause)
        {
            this.exactCause = exactCause;
            return this;
        }

        public Builder setEmail(String email)
        {
            this.email = email;
            return this;
        }

        public Builder setDomain(String domain)
        {
            this.domain = domain;
            return this;
        }

        public Builder setServer(String server)
        {
            this.server = server;
            return this;
        }

        public Builder setServerLine(String serverLine)
        {
            this.serverLine = serverLine;
            return this;
        }

        public RJMException build()
        {
            return new RJMException(this);
        }

    }

    private final ExactCause exactCause;
    private final String domain;
    private final String email;
    private final String server;
    private final String serverLine;

    /**
	 * Used to shut up eclipse build warnings
	 */
	private static final long serialVersionUID = 9459589695294L;

    private RJMException(Builder builder)
    {
        super(builder.message);
        exactCause = builder.exactCause;
        domain = builder.domain;
        email = builder.email;
        server = builder.server;
        serverLine = builder.serverLine;
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


}
