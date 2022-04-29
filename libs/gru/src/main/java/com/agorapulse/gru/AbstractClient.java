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

import com.agorapulse.gru.minions.Minion;
import com.agorapulse.testing.fixt.Fixt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClient implements Client {

    protected AbstractClient(Object unitTest) {
        this.unitTest = unitTest;
        this.unitTestClass = unitTest == null ? null : unitTest.getClass();
        this.fixt = Fixt.create(unitTestClass);
        this.fixt.mkdirs();
    }

    protected AbstractClient(Class<?> unitTestClass) {
        this.unitTest = null;
        this.unitTestClass = unitTestClass;
        this.fixt = Fixt.create(unitTestClass);
        this.fixt.mkdirs();
    }

    public final InputStream loadFixture(String fileName) {
        return fixt.readStream(fileName);
    }

    @Override
    public String getCurrentDescription() {
        StringBuilder builder = new StringBuilder(getRequest().getMethod());
        builder.append(" on ");
        if (getRequest().getBaseUri() != null) {
            builder.append(getRequest().getBaseUri());
        }
        if (getRequest().getUri() != null) {
            builder.append(getRequest().getUri());
        }
        return builder.toString();
    }

    @Override
    public List<Minion> getInitialSquad() {
        return new ArrayList<>();
    }

    @Override
    public Object getUnitTest() {
        return unitTest;
    }

    @Override
    public Class<?> getUnitTestClass() {
        return unitTestClass;
    }

    @Override
    public String toString() {
        return getCurrentDescription();
    }

    protected final Fixt fixt;
    private final Object unitTest;
    private final Class<?> unitTestClass;

}
