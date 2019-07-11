package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.cookie.Cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CookieMinion extends AbstractMinion<Client> {

    public CookieMinion() {
        super(Client.class);
    }

    @Override
    protected GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        for (Cookie cookie : requestCookies) {
            client.getRequest().addCookie(cookie);
        }

        return context;
    }

    @Override
    protected void doVerify(Client client, Squad squad, GruContext context) {
        List<Cookie> cookies = client.getResponse().getCookies();
        for (Cookie cookie : responseCookies) {
            if (cookies.stream().noneMatch(cookie::similarTo)) {
                throw new AssertionError("Missing response cookie " + cookie + "\nFound: " + cookies);
            }
        }
    }

    @Override
    public int getIndex() {
        return PARAMETERS_MINION_INDEX;
    }

    public List<Cookie> getRequestCookies() {
        return requestCookies;
    }

    public List<Cookie> getResponseCookies() {
        return responseCookies;
    }

    private final List<Cookie> requestCookies = new ArrayList<>();
    private final List<Cookie> responseCookies = new ArrayList<>();

}
