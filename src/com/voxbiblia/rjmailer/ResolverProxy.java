package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * A ResolverProxy implementation resolves MX queries using the DNS system.
 */
public interface ResolverProxy
{
    List resolveMX(String name);
}
