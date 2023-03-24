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
    public static final Matcher<String> ISO_DATE = matchesPattern("\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d(:[0-5]\\d(\\.\\d{1,10})?)?([+-][0-2]\\d:[0-5]\\d|Z)");

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
