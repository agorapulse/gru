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
