package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * A ResolverProxy implementation resolves MX queries using the DNS system.
 */
interface ResolverProxy
{
    List resolveMX(String name);
}
