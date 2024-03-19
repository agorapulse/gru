/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.OffsetDateTime;

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

        OffsetDateTime dateTime = OffsetDateTime.parse(item);
        return dateTime.isAfter(dateTime.minusSeconds(3600)) && dateTime.isBefore(dateTime.plusSeconds(60));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching ISO date time which is not older than one hour");
    }
}
