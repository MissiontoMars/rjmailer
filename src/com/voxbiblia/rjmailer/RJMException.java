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
        DOMAIN_NOT_FOUND, DOMAIN_INVALID, DOMAIN_FAILURE
    }

    public static class Builder
    {
        private String message;
        private ExactCause exactCause;
        private String email;
        private String domain;

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

        public RJMException build()
        {
            return new RJMException(this);
        }
    }

    private final ExactCause exactCause;
    private final String domain;
    private final String email;

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
}
