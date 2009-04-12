package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * A ResolverProxy implementation resolves MX queries using the DNS system.
 */
interface Resolver
{
    List<String> resolveMX(String name);
}
