package com.agorapulse.gru.jsonunit;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.net.MalformedURLException;
import java.net.URL;

public class MatchesUrl extends BaseMatcher<String> {

    public static final Matcher<String> INSTANCE = new MatchesUrl();

    private MatchesUrl(){}

    @Override
    public boolean matches(Object item) {
        if (item == null) {
            return false;
        }
        try {
            new URL(item.toString());
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string which is valid URL");
    }
}
