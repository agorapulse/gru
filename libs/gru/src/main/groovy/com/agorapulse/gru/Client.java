/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
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
package com.agorapulse.gru;

import com.agorapulse.gru.cookie.Cookie;
import com.agorapulse.gru.minions.Minion;

import java.io.InputStream;
import java.util.List;

public interface Client {

    Request getRequest();

    Response getResponse();

    void reset();

    GruContext run(Squad squad, GruContext context);

    InputStream loadFixture(String fileName);

    String getCurrentDescription();

    List<Minion> getInitialSquad();

    Object getUnitTest();

    interface Request {

        String getBaseUri();

        void setBaseUri(String baseUri);

        String getUri();

        void setUri(String uri);

        String getMethod();

        void setMethod(String method);

        void addHeader(String name, String value);

        default void addCookie(Cookie cookie) {
            addHeader("Cookie", cookie.toString());
        }

        void setJson(String jsonText);

        void setContent(String contentType, byte[] payload);

        void addParameter(String name, Object value);

        void setMultipart(MultipartDefinition definition);
    }

    interface Response {

        int getStatus();

        List<String> getHeaders(String name);

        String getText();

        String getRedirectUrl();

        default List<Cookie> getCookies() {
            return Cookie.parseAll(getHeaders("Set-Cookie"));
        }
    }

}
