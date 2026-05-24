/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2026 Agorapulse.
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
package com.agorapulse.gru.grails.minions;

import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.grails.Grails;
import com.agorapulse.gru.minions.AbstractMinion;

import java.util.Objects;

public class ForwardMinion extends AbstractMinion<Grails> {

    public static final int FORWARD_MINION_INDEX = HTTP_MINION_INDEX + 1000;

    private String forwardedUri;

    public ForwardMinion() {
        super(Grails.class);
    }

    @Override
    public int getIndex() {
        return FORWARD_MINION_INDEX;
    }

    public String getForwardedUri() {
        return forwardedUri;
    }

    public void setForwardedUri(String forwardedUri) {
        this.forwardedUri = forwardedUri;
    }

    @Override
    protected void doVerify(Grails grails, Squad squad, GruContext context) {
        if (forwardedUri != null) {
            String actual = grails.getUnitTest().getResponse().getForwardedUrl();
            if (!Objects.equals(forwardedUri, actual)) {
                throw new AssertionError("Expected forward to '" + forwardedUri + "' but was '" + actual + "'");
            }
        }
    }
}
