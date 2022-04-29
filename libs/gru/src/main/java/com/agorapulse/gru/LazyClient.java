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

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * Lazy initialized client. This class is not thread safe.
 */
public class LazyClient implements Client {

    public static Client create(Supplier<Client> clientSupplier) {
        return new LazyClient(clientSupplier);
    }

    private final Supplier<Client> clientSupplier;
    private Client client;

    private LazyClient(Supplier<Client> clientSupplier) {
        this.clientSupplier = clientSupplier;
    }

    @Override
    public Request getRequest() {
        return ensureClient().getRequest();
    }

    @Override
    public Response getResponse() {
        return ensureClient().getResponse();
    }

    @Override
    public void reset() {
        ensureClient().reset();
    }

    @Override
    public GruContext run(Squad squad, GruContext context) {
        return ensureClient().run(squad, context);
    }

    @Override
    public InputStream loadFixture(String fileName) {
        return ensureClient().loadFixture(fileName);
    }

    @Override
    public String getCurrentDescription() {
        return ensureClient().getCurrentDescription();
    }

    @Override
    public List<Minion> getInitialSquad() {
        return ensureClient().getInitialSquad();
    }

    @Override
    public Object getUnitTest() {
        return ensureClient().getUnitTest();
    }

    @Override
    public Class<?> getUnitTestClass() {
        return ensureClient().getUnitTestClass();
    }

    private Client ensureClient() {
        if (client == null) {
            client = clientSupplier.get();
        }
        return client;
    }
}
