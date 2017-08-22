package com.agorapulse.gru.jsonunit;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.regex.Pattern;

/**
 * Matche for pattern matching.
 */
public class MatchesPattern extends BaseMatcher<String> {

    /**
     * Matches ISO Date.
     */
    public static final Matcher<String> ISO_DATE = matchesPattern("\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d(:[0-5]\\d)?([+-][0-2]\\d:[0-5]\\d|Z)");

    /**
     * Matches any positive number integer string.
     */
    public static final Matcher<String> POSITIVE_NUMBER_STRING = matchesPattern("\\d+");

    /**
     * Creates new matcher for given pattern.
     * @param regexp pattern
     * @return new matcher for givne pattern
     */
    public static Matcher<String> matchesPattern(String regexp) {
        return new MatchesPattern(Pattern.compile(regexp));
    }

    private final Pattern pattern;

    private MatchesPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matches(Object item) {
        return item != null && pattern.matcher(item.toString()).matches();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching the pattern '" + pattern + "'");
    }
}
