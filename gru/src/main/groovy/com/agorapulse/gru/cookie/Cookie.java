/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.cookie;

import com.agorapulse.gru.ResponseCookieDefinition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is simplified <code>okhttp3.Cookie</code>.
 * <p>
 * An <a href="http://tools.ietf.org/html/rfc6265">RFC 6265</a> Cookie.
 *
 * <p>This class doesn't support additional attributes on cookies, like <a
 * href="https://code.google.com/p/chromium/issues/detail?id=232693">Chromium's Priority=HIGH
 * extension</a>.
 */
public final class Cookie {
    static final long MAX_DATE = 253402300799999L;
    static final TimeZone UTC = TimeZone.getTimeZone("GMT");

    private static final Pattern YEAR_PATTERN
        = Pattern.compile("(\\d{2,4})[^\\d]*");
    private static final Pattern MONTH_PATTERN
        = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    private static final Pattern DAY_OF_MONTH_PATTERN
        = Pattern.compile("(\\d{1,2})[^\\d]*");
    private static final Pattern TIME_PATTERN
        = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
    private static final DateFormat STANDARD_DATE_FORMAT;
    private static final String DEFAULT_DOMAIN = "localhost";
    private static final String DEFAULT_PATH = "/";

    static {
        DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        rfc1123.setLenient(false);
        rfc1123.setTimeZone(UTC);
        STANDARD_DATE_FORMAT = rfc1123;
    }

    private final String name;
    private final String value;
    private final long expiresAt;
    private final String domain;
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;

    private final boolean persistent; // True if 'expires' or 'max-age' is present.
    private final boolean hostOnly; // True unless 'domain' is present.

    private Cookie(String name, String value, long expiresAt, String domain, String path,
                   boolean secure, boolean httpOnly, boolean hostOnly, boolean persistent) {
        this.name = name;
        this.value = value;
        this.expiresAt = expiresAt;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.hostOnly = hostOnly;
        this.persistent = persistent;
    }

    Cookie(Builder builder) {
        if (builder.name == null) {
            throw new NullPointerException("builder.name == null");
        }

        if (builder.value == null) {
            throw new NullPointerException("builder.value == null");
        }

        if (builder.domain == null) {
            throw new NullPointerException("builder.domain == null");
        }

        this.name = builder.name;
        this.value = builder.value;
        this.expiresAt = builder.expiresAt;
        this.domain = builder.domain;
        this.path = builder.path;
        this.secure = builder.secure;
        this.httpOnly = builder.httpOnly;
        this.persistent = builder.persistent;
        this.hostOnly = builder.hostOnly;
    }

    /**
     * Returns a non-empty string with this cookie's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a possibly-empty string with this cookie's value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns true if this cookie does not expire at the end of the current session.
     */
    public boolean getPersistent() {
        return persistent;
    }

    /**
     * Returns the time that this cookie expires, in the same format as {@link
     * System#currentTimeMillis()}. This is December 31, 9999 if the cookie is {@linkplain
     * #getPersistent()} not persistent}, in which case it will expire at the end of the current session.
     *
     * <p>This may return a value less than the current time, in which case the cookie is already
     * expired. Webservers may return expired cookies as a mechanism to delete previously set cookies
     * that may or may not themselves be expired.
     */
    public long getExpiresAt() {
        return expiresAt;
    }

    /**
     * Returns true if this cookie's domain should be interpreted as a single host name, or false if
     * it should be interpreted as a pattern. This flag will be false if its {@code Set-Cookie} header
     * included a {@code domain} attribute.
     *
     * <p>For example, suppose the cookie's domain is {@code example.com}. If this flag is true it
     * matches <strong>only</strong> {@code example.com}. If this flag is false it matches {@code
     * example.com} and all subdomains including {@code api.example.com}, {@code www.example.com}, and
     * {@code beta.api.example.com}.
     */
    public boolean getHostOnly() {
        return hostOnly;
    }

    /**
     * Returns the cookie's domain. If {@link #getHostOnly()} returns true this is the only domain that
     * matches this cookie; otherwise it matches this domain and all subdomains.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Returns this cookie's path. This cookie matches URLs prefixed with path segments that match
     * this path's segments. For example, if this path is {@code /foo} this cookie matches requests to
     * {@code /foo} and {@code /foo/bar}, but not {@code /} or {@code /football}.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns true if this cookie should be limited to only HTTP APIs. In web browsers this prevents
     * the cookie from being accessible to scripts.
     */
    public boolean getHttpOnly() {
        return httpOnly;
    }

    /**
     * Returns true if this cookie should be limited to only HTTPS requests.
     */
    public boolean getSecure() {
        return secure;
    }

    /**
     * Returns all of the cookies from a set of HTTP response headers.
     */
    public static List<Cookie> parseAll(List<String> cookieStrings) {
        List<Cookie> cookies = null;

        for (int i = 0, size = cookieStrings.size(); i < size; i++) {
            Cookie cookie = Cookie.parse(cookieStrings.get(i));
            if (cookies == null) {
                cookies = new ArrayList<>();
            }
            if (cookie != null) {
                cookies.add(cookie);
            }
        }

        return cookies != null
            ? Collections.unmodifiableList(cookies)
            : Collections.<Cookie>emptyList();
    }

    /**
     * Attempt to parse a {@code Set-Cookie} HTTP header value {@code setCookie} as a cookie. Returns
     * null if {@code setCookie} is not a well-formed cookie.
     */
    public static Cookie parse(String setCookie) {
        return parse(System.currentTimeMillis(), setCookie);
    }

    static Cookie parse(long currentTimeMillis, String setCookie) {
        int pos = 0;
        int limit = setCookie.length();
        int cookiePairEnd = delimiterOffset(setCookie, pos, limit, ';');

        int pairEqualsSign = delimiterOffset(setCookie, pos, cookiePairEnd, '=');
        if (pairEqualsSign == cookiePairEnd) {
            return null;
        }

        String cookieName = trimSubstring(setCookie, pos, pairEqualsSign);
        if (cookieName.isEmpty() || indexOfControlOrNonAscii(cookieName) != -1) {
            return null;
        }

        String cookieValue = trimSubstring(setCookie, pairEqualsSign + 1, cookiePairEnd);
        if (indexOfControlOrNonAscii(cookieValue) != -1) {
            return null;
        }

        long expiresAt = MAX_DATE;
        long deltaSeconds = -1L;
        String domain = DEFAULT_DOMAIN;
        String path = DEFAULT_PATH;
        boolean secureOnly = false;
        boolean httpOnly = false;
        boolean hostOnly = true;
        boolean persistent = false;

        pos = cookiePairEnd + 1;
        while (pos < limit) {
            int attributePairEnd = delimiterOffset(setCookie, pos, limit, ';');

            int attributeEqualsSign = delimiterOffset(setCookie, pos, attributePairEnd, '=');
            String attributeName = trimSubstring(setCookie, pos, attributeEqualsSign);
            String attributeValue = attributeEqualsSign < attributePairEnd
                ? trimSubstring(setCookie, attributeEqualsSign + 1, attributePairEnd)
                : "";

            if (attributeName.equalsIgnoreCase("expires")) {
                try {
                    expiresAt = parseExpires(attributeValue, 0, attributeValue.length());
                    persistent = true;
                } catch (IllegalArgumentException e) {
                    // Ignore this attribute, it isn't recognizable as a date.
                }
            } else if (attributeName.equalsIgnoreCase("max-age")) {
                try {
                    deltaSeconds = parseMaxAge(attributeValue);
                    persistent = true;
                } catch (NumberFormatException e) {
                    // Ignore this attribute, it isn't recognizable as a max age.
                }
            } else if (attributeName.equalsIgnoreCase("domain")) {
                try {
                    domain = parseDomain(attributeValue);
                    hostOnly = false;
                } catch (IllegalArgumentException e) {
                    // Ignore this attribute, it isn't recognizable as a domain.
                }
            } else if (attributeName.equalsIgnoreCase("path")) {
                path = attributeValue;
            } else if (attributeName.equalsIgnoreCase("secure")) {
                secureOnly = true;
            } else if (attributeName.equalsIgnoreCase("httponly")) {
                httpOnly = true;
            }

            pos = attributePairEnd + 1;
        }

        // If 'Max-Age' is present, it takes precedence over 'Expires', regardless of the order the two
        // attributes are declared in the cookie string.
        if (deltaSeconds == Long.MIN_VALUE) {
            expiresAt = Long.MIN_VALUE;
        } else if (deltaSeconds != -1L) {
            long deltaMilliseconds = deltaSeconds <= (Long.MAX_VALUE / 1000)
                ? deltaSeconds * 1000
                : Long.MAX_VALUE;
            expiresAt = currentTimeMillis + deltaMilliseconds;
            if (expiresAt < currentTimeMillis || expiresAt > MAX_DATE) {
                expiresAt = MAX_DATE; // Handle overflow & limit the date range.
            }
        }

        return new Cookie(cookieName, cookieValue, expiresAt, domain, path, secureOnly, httpOnly, hostOnly, persistent);
    }

    /**
     * Parse a date as specified in RFC 6265, section 5.1.1.
     */
    private static long parseExpires(String s, int pos, int limit) {
        pos = dateCharacterOffset(s, pos, limit, false);

        int hour = -1;
        int minute = -1;
        int second = -1;
        int dayOfMonth = -1;
        int month = -1;
        int year = -1;
        Matcher matcher = TIME_PATTERN.matcher(s);

        while (pos < limit) {
            int end = dateCharacterOffset(s, pos + 1, limit, true);
            matcher.region(pos, end);

            if (hour == -1 && matcher.usePattern(TIME_PATTERN).matches()) {
                hour = Integer.parseInt(matcher.group(1));
                minute = Integer.parseInt(matcher.group(2));
                second = Integer.parseInt(matcher.group(3));
            } else if (dayOfMonth == -1 && matcher.usePattern(DAY_OF_MONTH_PATTERN).matches()) {
                dayOfMonth = Integer.parseInt(matcher.group(1));
            } else if (month == -1 && matcher.usePattern(MONTH_PATTERN).matches()) {
                String monthString = matcher.group(1).toLowerCase(Locale.US);
                month = MONTH_PATTERN.pattern().indexOf(monthString) / 4; // Sneaky! jan=1, dec=12.
            } else if (year == -1 && matcher.usePattern(YEAR_PATTERN).matches()) {
                year = Integer.parseInt(matcher.group(1));
            }

            pos = dateCharacterOffset(s, end + 1, limit, false);
        }

        // Convert two-digit years into four-digit years. 99 becomes 1999, 15 becomes 2015.
        if (year >= 70 && year <= 99) {
            year += 1900;
        }

        if (year >= 0 && year <= 69) {
            year += 2000;
        }

        // If any partial is omitted or out of range, return -1. The date is impossible. Note that leap
        // seconds are not supported by this syntax.
        if (year < 1601) {
            throw new IllegalArgumentException();
        }

        if (month == -1) {
            throw new IllegalArgumentException();
        }

        if (dayOfMonth < 1 || dayOfMonth > 31) {
            throw new IllegalArgumentException();
        }

        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException();
        }

        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException();
        }

        if (second < 0 || second > 59) {
            throw new IllegalArgumentException();
        }

        Calendar calendar = new GregorianCalendar(UTC);
        calendar.setLenient(false);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Returns the index of the next date character in {@code input}, or if {@code invert} the index
     * of the next non-date character in {@code input}.
     */
    private static int dateCharacterOffset(String input, int pos, int limit, boolean invert) {
        for (int i = pos; i < limit; i++) {
            int c = input.charAt(i);
            boolean dateCharacter = (c < ' ' && c != '\t') || (c >= '\u007f')
                || (c >= '0' && c <= '9')
                || (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c == ':');
            if (dateCharacter == !invert) {
                return i;
            }
        }
        return limit;
    }

    /**
     * Returns the positive value if {@code attributeValue} is positive, or {@link Long#MIN_VALUE} if
     * it is either 0 or negative. If the value is positive but out of range, this returns {@link
     * Long#MAX_VALUE}.
     *
     * @throws NumberFormatException if {@code s} is not an integer of any precision.
     */
    private static long parseMaxAge(String s) {
        try {
            long parsed = Long.parseLong(s);
            return parsed <= 0L ? Long.MIN_VALUE : parsed;
        } catch (NumberFormatException e) {
            // Check if the value is an integer (positive or negative) that's too big for a long.
            if (s.matches("-?\\d+")) {
                return s.startsWith("-") ? Long.MIN_VALUE : Long.MAX_VALUE;
            }
            throw e;
        }
    }

    /**
     * Returns a domain string like {@code example.com} for an input domain like {@code EXAMPLE.COM}
     * or {@code .example.com}.
     */
    private static String parseDomain(String s) {
        if (s.endsWith(".")) {
            throw new IllegalArgumentException();
        }
        if (s.startsWith(".")) {
            return s.substring(1);
        }
        return s;
    }

    /**
     * Returns the index of the first character in {@code input} that is {@code delimiter}. Returns
     * limit if there is no such character.
     */
    private static int delimiterOffset(String input, int pos, int limit, char delimiter) {
        for (int i = pos; i < limit; i++) {
            if (input.charAt(i) == delimiter) {
                return i;
            }
        }
        return limit;
    }

    /**
     * Equivalent to {@code string.substring(pos, limit).trim()}.
     */
    private static String trimSubstring(String string, int pos, int limit) {
        int start = skipLeadingAsciiWhitespace(string, pos, limit);
        int end = skipTrailingAsciiWhitespace(string, start, limit);
        return string.substring(start, end);
    }


    /**
     * Increments {@code pos} until {@code input[pos]} is not ASCII whitespace. Stops at {@code
     * limit}.
     */
    private static int skipLeadingAsciiWhitespace(String input, int pos, int limit) {
        for (int i = pos; i < limit; i++) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    continue;
                default:
                    return i;
            }
        }
        return limit;
    }

    /**
     * Decrements {@code limit} until {@code input[limit - 1]} is not ASCII whitespace. Stops at
     * {@code pos}.
     */
    private static int skipTrailingAsciiWhitespace(String input, int pos, int limit) {
        for (int i = limit - 1; i >= pos; i--) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    continue;
                default:
                    return i + 1;
            }
        }
        return pos;
    }

    /**
     * Returns the index of the first character in {@code input} that is either a control character
     * (like {@code \u0000 or \n}) or a non-ASCII character. Returns -1 if {@code input} has no such
     * characters.
     */
    private static int indexOfControlOrNonAscii(String input) {
        for (int i = 0, length = input.length(); i < length; i++) {
            char c = input.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                return i;
            }
        }
        return -1;
    }

    /**
     * Builds a cookie. The {@linkplain #getName() name}, {@linkplain #getValue() value}, and {@linkplain
     * #getDomain() domain} values must all be set before calling {@link #build}.
     */
    public static final class Builder implements ResponseCookieDefinition {
        String name;
        String value;
        long expiresAt = MAX_DATE;
        String domain = DEFAULT_DOMAIN;
        String path = DEFAULT_PATH;
        boolean secure;
        boolean httpOnly;
        boolean persistent;
        boolean hostOnly = true;

        public Builder name(String name) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            }

            if (!name.trim().equals(name)) {
                throw new IllegalArgumentException("name is not trimmed");
            }

            this.name = name;
            return this;
        }

        public Builder value(String value) {
            if (value == null) {
                throw new IllegalArgumentException("value == null");
            }

            if (!value.trim().equals(value)) {
                throw new IllegalArgumentException("value is not trimmed");
            }

            this.value = value;
            return this;
        }

        public Builder expiresAt(long expiresAt) {
            if (expiresAt <= 0) {
                expiresAt = Long.MIN_VALUE;
            }

            if (expiresAt > MAX_DATE) {
                expiresAt = MAX_DATE;
            }

            this.expiresAt = expiresAt;
            this.persistent = true;
            return this;
        }

        /**
         * Set the domain pattern for this cookie. The cookie will match {@code domain} and all of its
         * subdomains.
         */
        public Builder domain(String domain) {
            return domain(domain, false);
        }

        /**
         * Set the host-only domain for this cookie. The cookie will match {@code domain} but none of
         * its subdomains.
         */
        public Builder hostOnlyDomain(String domain) {
            return domain(domain, true);
        }

        private Builder domain(String domain, boolean hostOnly) {
            if (domain == null) {
                throw new IllegalArgumentException("domain == null");
            }

            this.domain = domain;
            this.hostOnly = hostOnly;
            return this;
        }

        public Builder path(String path) {
            if (path == null) {
                this.path = "/";
                return this;
            }
            if (!path.startsWith("/")) {
                throw new IllegalArgumentException("path must start with '/'");
            }
            this.path = path;
            return this;
        }

        public Builder secure(boolean isSecure) {
            this.secure = isSecure;
            return this;
        }

        public Builder httpOnly(boolean isHttpOnly) {
            this.httpOnly = isHttpOnly;
            return this;
        }

        public Cookie build() {
            return new Cookie(this);
        }
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * @param forObsoleteRfc2965 true to include a leading {@code .} on the domain pattern. This is
     *                           necessary for {@code example.com} to match {@code www.example.com} under RFC 2965. This
     *                           extra dot is ignored by more recent specifications.
     */
    String toString(boolean forObsoleteRfc2965) {
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append('=');
        result.append(value);

        if (persistent) {
            if (expiresAt == Long.MIN_VALUE) {
                result.append("; max-age=0");
            } else {
                result.append("; expires=").append(STANDARD_DATE_FORMAT.format(new Date(expiresAt)));
            }
        }

        if (!hostOnly) {
            result.append("; domain=");
            if (forObsoleteRfc2965) {
                result.append(".");
            }
            result.append(domain);
        }

        result.append("; path=").append(path);

        if (secure) {
            result.append("; secure");
        }

        if (httpOnly) {
            result.append("; httponly");
        }

        return result.toString();
    }

    /**
     * Check if any non-default value  of this cookies is different from the other cookie.
     *
     * @param other the other cookie
     * @return false if any non-default value  of this cookies is different from the other cookie.
     */
    public boolean similarTo(Cookie other) {
        if (name != null && !name.equals(other.name)) {
            return false;
        }

        if (value != null && !value.equals(other.value)) {
            return false;
        }

        if (domain != null && !DEFAULT_DOMAIN.equals(domain) && !DEFAULT_DOMAIN.equals(other.domain) && !domain.equals(other.domain)) {
            return false;
        }

        if (path != null && !DEFAULT_PATH.equals(path) && !DEFAULT_PATH.equals(other.path) && !path.equals(other.path)) {
            return false;
        }

        if (MAX_DATE != expiresAt && expiresAt != other.expiresAt) {
            return false;
        }

        if (secure != other.secure) {
            return false;
        }

        if (httpOnly != other.httpOnly) {
            return false;

        }

        return persistent == other.persistent;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Cookie)) {
            return false;
        }

        Cookie that = (Cookie) other;
        return that.name.equals(name)
            && that.value.equals(value)
            && that.domain.equals(domain)
            && that.path.equals(path)
            && that.expiresAt == expiresAt
            && that.secure == secure
            && that.httpOnly == httpOnly
            && that.persistent == persistent
            && that.hostOnly == hostOnly;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + name.hashCode();
        hash = 31 * hash + value.hashCode();
        hash = 31 * hash + domain.hashCode();
        hash = 31 * hash + path.hashCode();
        hash = 31 * hash + (int) (expiresAt ^ (expiresAt >>> 32));
        hash = 31 * hash + (secure ? 0 : 1);
        hash = 31 * hash + (httpOnly ? 0 : 1);
        hash = 31 * hash + (persistent ? 0 : 1);
        hash = 31 * hash + (hostOnly ? 0 : 1);
        return hash;
    }
}
