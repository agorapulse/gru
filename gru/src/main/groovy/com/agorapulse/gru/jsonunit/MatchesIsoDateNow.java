package com.agorapulse.gru.jsonunit;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

/**
 * Matcher to match ISO date which is within one hour before or after current time.
 */
public class MatchesIsoDateNow extends TypeSafeMatcher<String> {

    public static final Matcher<String> INSTANCE = new MatchesIsoDateNow();

    @Override
    protected boolean matchesSafely(String item) {
        if (!MatchesPattern.ISO_DATE.matches(item)) {
            return false;
        }

        DateTime dateTime = new DateTime(item);
        return dateTime.isAfter(dateTime.minusHours(1)) && dateTime.isBefore(dateTime.plusMinutes(1));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching ISO date time which is not older than one hour");
    }
}
