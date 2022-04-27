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
package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;

/**
 * Abstract class which stubs every method except Minion#getOrder() so subclasses may only implement the ones they are
 * interested in.
 * @param <C> type of the client
 */
public abstract class AbstractMinion<C extends Client> implements Minion {

    private final Class<C> clientType;

    protected AbstractMinion(Class<C> clientType) {
        this.clientType = clientType;
    }

    @Override
    public final GruContext beforeRun(Client client, Squad squad, GruContext context) {
        try {
            C c = clientType.cast(client);
            return doBeforeRun(c, squad, context);
        } catch (ClassCastException e) {
            return context.withError(e);
        }
    }

    @Override
    public final GruContext afterRun(Client client, Squad squad, GruContext context) {
        try {
            C c = clientType.cast(client);
            return doAfterRun(c, squad, context);
        } catch (ClassCastException e) {
            return context.withError(e);
        }
    }

    @Override
    public final void verify(Client client, Squad squad, GruContext context) throws Throwable {
        try {
            C c = clientType.cast(client);
            doVerify(c, squad, context);
        } catch (ClassCastException e) {
            throw new AssertionError(e);
        }

    }

    protected GruContext doBeforeRun(C client, Squad squad, GruContext context) {
        return context;
    }

    protected GruContext doAfterRun(C client, Squad squad, GruContext context) {
        return context;
    }

    protected void doVerify(C client, Squad squad, GruContext context) throws Throwable {}

}
