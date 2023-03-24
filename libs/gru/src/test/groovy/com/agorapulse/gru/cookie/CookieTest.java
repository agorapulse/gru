/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2023 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.cookie;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import static com.agorapulse.gru.cookie.Cookie.MAX_DATE;
import static com.agorapulse.gru.cookie.Cookie.UTC;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Inspired by CookieTest from okhttp3.
 */
final class CookieTest {

    @Test
    void simpleCookie() throws Exception {
        Cookie cookie = Cookie.parse("SID=31d4d96e407aad42");
        assertThat(cookie.toString()).isEqualTo("SID=31d4d96e407aad42; path=/");
    }

    @Test
    void noEqualsSign() throws Exception {
        assertThat(Cookie.parse("foo")).isNull();
        assertThat(Cookie.parse("foo; Path=/")).isNull();
    }

    @Test
    void emptyName() throws Exception {
        assertThat(Cookie.parse("=b")).isNull();
        assertThat(Cookie.parse(" =b")).isNull();
        assertThat(Cookie.parse("\r\t \n=b")).isNull();
    }

    @Test
    void spaceInName() throws Exception {
        assertThat(Cookie.parse("a b=cd").getName()).isEqualTo("a b");
    }

    @Test
    void spaceInValue() throws Exception {
        assertThat(Cookie.parse("ab=c d").getValue()).isEqualTo("c d");
    }

    @Test
    void trimLeadingAndTrailingWhitespaceFromName() throws Exception {
        assertThat(Cookie.parse(" a=b").getName()).isEqualTo("a");
        assertThat(Cookie.parse("a =b").getName()).isEqualTo("a");
        assertThat(Cookie.parse("\r\t \na\n\t \n=b").getName()).isEqualTo("a");
    }

    @Test
    void emptyValue() throws Exception {
        assertThat(Cookie.parse("a=").getValue()).isEqualTo("");
        assertThat(Cookie.parse("a= ").getValue()).isEqualTo("");
        assertThat(Cookie.parse("a=\r\t \n").getValue()).isEqualTo("");
    }

    @Test
    void trimLeadingAndTrailingWhitespaceFromValue() throws Exception {
        assertThat(Cookie.parse("a= ").getValue()).isEqualTo("");
        assertThat(Cookie.parse("a= b").getValue()).isEqualTo("b");
        assertThat(Cookie.parse("a=b ").getValue()).isEqualTo("b");
        assertThat(Cookie.parse("a=\r\t \nb\n\t \n").getValue()).isEqualTo("b");
    }

    @Test
    void invalidCharacters() throws Exception {
        assertThat(Cookie.parse("a\u0000b=cd")).isNull();
        assertThat(Cookie.parse("ab=c\u0000d")).isNull();
        assertThat(Cookie.parse("a\u0001b=cd")).isNull();
        assertThat(Cookie.parse("ab=c\u0001d")).isNull();
        assertThat(Cookie.parse("a\u0009b=cd")).isNull();
        assertThat(Cookie.parse("ab=c\u0009d")).isNull();
        assertThat(Cookie.parse("a\u001fb=cd")).isNull();
        assertThat(Cookie.parse("ab=c\u001fd")).isNull();
        assertThat(Cookie.parse("a\u007fb=cd")).isNull();
        assertThat(Cookie.parse("ab=c\u007fd")).isNull();
        assertThat(Cookie.parse("a\u0080b=cd")).isNull();
        assertThat(Cookie.parse("ab=c\u0080d")).isNull();
        assertThat(Cookie.parse("a\u00ffb=cd")).isNull();
        assertThat(Cookie.parse("ab=c\u00ffd")).isNull();
    }

    @Test
    void maxAge() throws Exception {
        assertThat(parseCookie(50000L, "a=b; Max-Age=1").getExpiresAt())
            .isEqualTo(51000L);
        assertThat(parseCookie(50000L, "a=b; Max-Age=9223372036854724").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(parseCookie(50000L, "a=b; Max-Age=9223372036854725").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(parseCookie(50000L, "a=b; Max-Age=9223372036854726").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(parseCookie(9223372036854773807L, "a=b; Max-Age=1").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(parseCookie(9223372036854773807L, "a=b; Max-Age=2").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(parseCookie(9223372036854773807L, "a=b; Max-Age=3").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(parseCookie(50000L, "a=b; Max-Age=10000000000000000000").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(parseCookie(50000L, "a=b; Max-Age=For-Eva").getExpiresAt())
            .isEqualTo(MAX_DATE);
    }

    @Test
    void maxAgeNonPositive() throws Exception {
        assertThat(parseCookie(50000L, "a=b; Max-Age=-1").getExpiresAt())
            .isEqualTo(Long.MIN_VALUE);
        assertThat(parseCookie(50000L, "a=b; Max-Age=0").getExpiresAt())
            .isEqualTo(Long.MIN_VALUE);
        assertThat(parseCookie(50000L, "a=b; Max-Age=-9223372036854775808").getExpiresAt())
            .isEqualTo(Long.MIN_VALUE);
        assertThat(parseCookie(50000L, "a=b; Max-Age=-9223372036854775809").getExpiresAt())
            .isEqualTo(Long.MIN_VALUE);
        assertThat(parseCookie(50000L, "a=b; Max-Age=-10000000000000000000").getExpiresAt())
            .isEqualTo(Long.MIN_VALUE);
    }

    @Test
    void domainAndPath() throws Exception {
        Cookie cookie = Cookie.parse("SID=31d4d96e407aad42; Path=/; Domain=.example.com");
        assertThat(cookie.getDomain()).isEqualTo("example.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getHostOnly()).isFalse();
        assertThat(cookie.toString()).isEqualTo(
            "SID=31d4d96e407aad42; domain=example.com; path=/");
    }

    @Test
    void invalidDomain() throws Exception {
        Cookie cookie = Cookie.parse("SID=31d4d96e407aad42; Path=/; Domain=example.com.");
        assertThat(cookie.getDomain()).isEqualTo(Cookie.DEFAULT_DOMAIN);
    }

    @Test
    void secureAndHttpOnly() throws Exception {
        Cookie cookie = Cookie.parse("SID=31d4d96e407aad42; Path=/; Secure; HttpOnly");
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getHttpOnly()).isTrue();
        assertThat(cookie.toString()).isEqualTo(
            "SID=31d4d96e407aad42; path=/; secure; httponly");
    }

    @Test
    void expiresDate() throws Exception {
        assertThat(new Date(Cookie.parse("a=b; Expires=Thu, 01 Jan 1970 00:00:00 GMT")
            .getExpiresAt())).isEqualTo(date("1970-01-01T00:00:00.000+0000"));
        assertThat(new Date(Cookie.parse("a=b; Expires=Wed, 09 Jun 2021 10:18:14 GMT")
            .getExpiresAt())).isEqualTo(date("2021-06-09T10:18:14.000+0000"));
        assertThat(new Date(Cookie.parse("a=b; Expires=Sun, 06 Nov 1994 08:49:37 GMT")
            .getExpiresAt())).isEqualTo(date("1994-11-06T08:49:37.000+0000"));
        assertThat(new Date(Cookie.parse("a=b; Expires=Sun, 06 Nov 68 08:49:37 GMT")
            .getExpiresAt())).isEqualTo(date("2068-11-06T08:49:37.000+0000"));
    }

    @Test
    void awkwardDates() throws Exception {
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 70 00:00:00 GMT").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 January 1970 00:00:00 GMT").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Janucember 1970 00:00:00 GMT").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=Thu, 1 Jan 1970 00:00:00 GMT").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 1970 0:00:00 GMT").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 1970 00:0:00 GMT").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 1970 00:00:0 GMT").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=00:00:00 Thu, 01 Jan 1970 GMT").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=00:00:00 1970 Jan 01").getExpiresAt())
            .isEqualTo(0L);
        assertThat(Cookie.parse("a=b; Expires=00:00:00 1970 Jan 1").getExpiresAt())
            .isEqualTo(0L);
    }

    @Test
    void invalidYear() throws Exception {
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 1600 00:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 19999 00:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 00:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
    }

    @Test
    void invalidMonth() throws Exception {
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Foo 1970 00:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Foocember 1970 00:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 1970 00:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
    }

    @Test
    void invalidDayOfMonth() throws Exception {
        assertThat(Cookie.parse("a=b; Expires=Thu, 32 Jan 1970 00:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
        assertThat(Cookie.parse("a=b; Expires=Thu, Jan 1970 00:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
    }

    @Test
    void invalidHour() throws Exception {
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 1970 24:00:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
    }

    @Test
    void invalidMinute() throws Exception {
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 1970 00:60:00 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
    }

    @Test
    void invalidSecond() throws Exception {
        assertThat(Cookie.parse("a=b; Expires=Thu, 01 Jan 1970 00:00:60 GMT").getExpiresAt())
            .isEqualTo(MAX_DATE);
    }

    @Test
    void hostOnly() throws Exception {
        assertThat(Cookie.parse("a=b").getHostOnly()).isTrue();
        assertThat(Cookie.parse("a=b; domain=example.com").getHostOnly()).isFalse();
    }

    @Test
    void httpOnly() throws Exception {
        assertThat(Cookie.parse("a=b").getHttpOnly()).isFalse();
        assertThat(Cookie.parse("a=b; HttpOnly").getHttpOnly()).isTrue();
    }

    @Test
    void secure() throws Exception {
        assertThat(Cookie.parse("a=b").getSecure()).isFalse();
        assertThat(Cookie.parse("a=b; Secure").getSecure()).isTrue();
    }

    @Test
    void maxAgeTakesPrecedenceOverExpires() throws Exception {
        // Max-Age = 1, Expires = 2. In either order.
        assertThat(parseCookie(
            0L, "a=b; Max-Age=1; Expires=Thu, 01 Jan 1970 00:00:02 GMT").getExpiresAt()).isEqualTo(
            1000L);
        assertThat(parseCookie(
            0L, "a=b; Expires=Thu, 01 Jan 1970 00:00:02 GMT; Max-Age=1").getExpiresAt()).isEqualTo(
            1000L);
        // Max-Age = 2, Expires = 1. In either order.
        assertThat(parseCookie(
            0L, "a=b; Max-Age=2; Expires=Thu, 01 Jan 1970 00:00:01 GMT").getExpiresAt()).isEqualTo(
            2000L);
        assertThat(parseCookie(
            0L, "a=b; Expires=Thu, 01 Jan 1970 00:00:01 GMT; Max-Age=2").getExpiresAt()).isEqualTo(
            2000L);
    }

    /**
     * If a cookie incorrectly defines multiple 'Max-Age' attributes, the last one defined wins.
     */
    @Test
    void lastMaxAgeWins() throws Exception {
        assertThat(parseCookie(
            0L, "a=b; Max-Age=2; Max-Age=4; Max-Age=1; Max-Age=3").getExpiresAt()).isEqualTo(3000L);
    }

    /**
     * If a cookie incorrectly defines multiple 'Expires' attributes, the last one defined wins.
     */
    @Test
    void lastExpiresAtWins() throws Exception {
        assertThat(parseCookie(0L, "a=b; "
            + "Expires=Thu, 01 Jan 1970 00:00:02 GMT; "
            + "Expires=Thu, 01 Jan 1970 00:00:04 GMT; "
            + "Expires=Thu, 01 Jan 1970 00:00:01 GMT; "
            + "Expires=Thu, 01 Jan 1970 00:00:03 GMT").getExpiresAt()).isEqualTo(3000L);
    }

    @Test
    void maxAgeOrExpiresMakesCookiePersistent() throws Exception {
        assertThat(parseCookie(0L, "a=b").getPersistent()).isFalse();
        assertThat(parseCookie(0L, "a=b; Max-Age=1").getPersistent()).isTrue();
        assertThat(parseCookie(0L, "a=b; Expires=Thu, 01 Jan 1970 00:00:01 GMT").getPersistent())
            .isTrue();
    }

    @Test
    void builder() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .domain("example.com")
            .build();
        assertThat(cookie.getName()).isEqualTo("a");
        assertThat(cookie.getValue()).isEqualTo("b");
        assertThat(cookie.getExpiresAt()).isEqualTo(MAX_DATE);
        assertThat(cookie.getDomain()).isEqualTo("example.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getSecure()).isFalse();
        assertThat(cookie.getHttpOnly()).isFalse();
        assertThat(cookie.getPersistent()).isFalse();
        assertThat(cookie.getHostOnly()).isFalse();
    }

    @Test
    void builderNameValidation() throws Exception {
        try {
            new Cookie.Builder().name(null);
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            new Cookie.Builder().name(" a ");
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    void builderValueValidation() throws Exception {
        try {
            new Cookie.Builder().value(null);
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            new Cookie.Builder().value(" b ");
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    void builderClampsMaxDate() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .hostOnlyDomain("example.com")
            .expiresAt(Long.MAX_VALUE)
            .build();
        assertThat(cookie.toString()).isEqualTo(
            "a=b; expires=Fri, 31 Dec 9999 23:59:59 GMT; path=/");
    }

    @Test
    void builderExpiresAt() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .hostOnlyDomain("example.com")
            .expiresAt(date("1970-01-01T00:00:01.000+0000").getTime())
            .build();
        assertThat(cookie.toString()).isEqualTo(
            "a=b; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/");
    }

    @Test
    void builderClampsMinDate() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .hostOnlyDomain("example.com")
            .expiresAt(date("1970-01-01T00:00:00.000+0000").getTime())
            .build();
        assertThat(cookie.toString()).isEqualTo("a=b; max-age=0; path=/");
    }

    @Test
    void builderDomainValidation() throws Exception {
        try {
            new Cookie.Builder().hostOnlyDomain(null);
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    void builderDomain() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .hostOnlyDomain("squareup.com")
            .build();
        assertThat(cookie.getDomain()).isEqualTo("squareup.com");
        assertThat(cookie.getHostOnly()).isTrue();
    }

    @Test
    void builderPath() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .hostOnlyDomain("example.com")
            .path("/foo")
            .build();
        assertThat(cookie.getPath()).isEqualTo("/foo");
    }

    @Test
    void builderPathValidation() throws Exception {
        try {
            new Cookie.Builder().path("foo");
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    void builderSecure() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .hostOnlyDomain("example.com")
            .secure(true)
            .build();
        assertThat(cookie.getSecure()).isTrue();
    }

    @Test
    void builderHttpOnly() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .hostOnlyDomain("example.com")
            .httpOnly(true)
            .build();
        assertThat(cookie.getHttpOnly()).isTrue();
    }

    @Test
    void builderIpv6() throws Exception {
        Cookie cookie = new Cookie.Builder()
            .name("a")
            .value("b")
            .domain("0:0:0:0:0:0:0:1")
            .build();
        assertThat(cookie.getDomain()).isEqualTo("0:0:0:0:0:0:0:1");
    }

    @Test
    void equalsAndHashCode() throws Exception {
        List<String> cookieStrings = asList(
            "a=b; Path=/c; Domain=example.com; Max-Age=5; Secure; HttpOnly",
            "a= ; Path=/c; Domain=example.com; Max-Age=5; Secure; HttpOnly",
            "a=b;          Domain=example.com; Max-Age=5; Secure; HttpOnly",
            "a=b; Path=/c;                     Max-Age=5; Secure; HttpOnly",
            "a=b; Path=/c; Domain=example.com;            Secure; HttpOnly",
            "a=b; Path=/c; Domain=example.com; Max-Age=5;         HttpOnly",
            "a=b; Path=/c; Domain=example.com; Max-Age=5; Secure;         "
        );
        for (String stringA : cookieStrings) {
            Cookie cookieA = parseCookie(0, stringA);
            for (String stringB : cookieStrings) {
                Cookie cookieB = parseCookie(0, stringB);
                if (Objects.equals(stringA, stringB)) {
                    assertThat(cookieB.hashCode()).isEqualTo(cookieA.hashCode());
                    assertThat(cookieB).isEqualTo(cookieA);
                } else {
                    assertThat(cookieB.hashCode()).isNotEqualTo((long) cookieA.hashCode());
                    assertThat(cookieB).isNotEqualTo(cookieA);
                }
            }
            assertThat(cookieA).isNotEqualTo(null);
        }
    }

    private static Cookie parseCookie(long time, String cookieString) {
        return Cookie.parse(time, cookieString);
    }

    private Date date(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        format.setTimeZone(UTC);
        return format.parse(s);
    }
}
