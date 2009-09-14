package com.voxbiblia.rjmailer;

/**
 * Enumerates the exact causes to an RJMException
 */
public enum ExactCause
{
    DOMAIN_NOT_FOUND, DOMAIN_INVALID, DOMAIN_FAILURE,
    SMTP_CONNECT, SMTP_UNEXPECTED_STATUS,
    INVALID_INPUT, ALL_SERVERS_FAILED, IO_EXCEPTION,
    ADDRESS_SYNTAX, RECIPIENT_MISSING
}
