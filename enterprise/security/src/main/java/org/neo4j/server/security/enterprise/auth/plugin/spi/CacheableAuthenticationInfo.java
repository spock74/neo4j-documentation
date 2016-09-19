/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.server.security.enterprise.auth.plugin.spi;

import java.util.Map;

/**
 * A cacheable object that can be returned as the result of successful authentication by an
 * <tt>AuthenticationPlugin</tt>.
 *
 * <p>This object can be cached by the authentication cache within the realm
 * that the authentication plugin is connected to.
 *
 * <p>This is an alternative to <tt>CustomCacheableAuthenticationInfo</tt> if you want Neo4j to manage secure
 * hashing and matching of cached credentials.
 *
 * <p>NOTE: Caching only occurs if it is explicitly enabled by the plugin.
 *
 * @see AuthenticationPlugin#authenticate(Map)
 * @see org.neo4j.server.security.enterprise.auth.plugin.api.RealmOperations#setAuthenticationCachingEnabled(boolean)
 * @see CustomCacheableAuthenticationInfo
 */
public interface CacheableAuthenticationInfo extends AuthenticationInfo
{
    /**
     * Should return a principal that uniquely identifies the authenticated subject within this realm.
     * This will be used as the cache key, and needs to be matcheable against a principal within the auth token map.
     *
     * <p>Typically this is the same as the principal within the auth token map.
     *
     * @return a principal that uniquely identifies the authenticated subject within this realm
     *
     * @see org.neo4j.server.security.enterprise.auth.plugin.api.AuthToken#PRINCIPAL
     */
    @Override
    Object getPrincipal();

    /**
     * Should return credentials that can be cached, so that successive authentication attempts could be performed
     * against the cached authentication info from a previous successful authentication attempt, without having to
     * call <tt>AuthenticationInfo.getAuthenticationInfo()</tt> again.
     *
     * <p>NOTE: The returned credentials will be hashed using a cryptographic hash function together
     * with a random salt (generated with a secure random number generator) before being stored.
     *
     * @return credentials that can be cached
     *
     * @see org.neo4j.server.security.enterprise.auth.plugin.api.AuthToken#CREDENTIALS
     * @see AuthenticationPlugin#authenticate(Map)
     */
    byte[] getCredentials();

    /**
     * Creates a new <tt>CacheableAuthenticationInfo</tt>
     *
     * @param principal a principal that uniquely identifies the authenticated subject within this realm
     * @param credentials credentials that can be cached
     * @return a new <tt>CacheableAuthenticationInfo</tt> containing the given parameters.
     */
    static CacheableAuthenticationInfo of( Object principal, byte[] credentials )
    {
        return new CacheableAuthenticationInfo()
        {
            @Override
            public Object getPrincipal()
            {
                return principal;
            }

            @Override
            public byte[] getCredentials()
            {
                return credentials;
            }
        };
    }
}