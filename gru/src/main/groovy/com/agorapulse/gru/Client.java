package com.agorapulse.gru;

import com.agorapulse.gru.minions.Minion;

import java.io.InputStream;
import java.util.List;

public interface Client {

    Request getRequest();

    Response getResponse();

    void reset();

    GruContext run(Squad squad, GruContext context);

    String getFixtureLocation(String relativeFilePath);

    InputStream loadFixture(String fileName);

    String getCurrentDescription();

    List<Minion> getInitialSquad();

    Object getUnitTest();

    interface Request {

        String getUri();

        void setUri(String uri);

        String getMethod();

        void setMethod(String method);

        void addHeader(String name, String value);

        void setJson(String jsonText);

        void addParameter(String name, Object value);
    }

    interface Response {

        int getStatus();

        List<String> getHeaders(String name);

        String getText();

        String getRedirectUrl();
    }

}
