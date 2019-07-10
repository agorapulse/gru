package com.agorapulse.gru;

public interface ResponseCookieDefinition {

    ResponseCookieDefinition name(String name);

    ResponseCookieDefinition value(String value);

    ResponseCookieDefinition expiresAt(long expiration);

    ResponseCookieDefinition domain(String domain);

    ResponseCookieDefinition hostOnlyDomain(String domain);

    ResponseCookieDefinition path(String path);

    ResponseCookieDefinition secure(boolean isSecure);

    ResponseCookieDefinition httpOnly(boolean isHttpOnly);

}
