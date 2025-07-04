/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
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

/**
 * Initialization controller ensures that Controller under test is initialized.
 */
public class ControllerInitializationMinion extends AbstractMinion<Grails> {

    public ControllerInitializationMinion() {
        super(Grails.class);
    }

    @Override
    public int getIndex() {
        return INITIALIZATION_MINION_INDEX;
    }

    @Override
    protected GruContext doBeforeRun(Grails client, Squad squad, GruContext context) {
        client.getUnitTest().getController();
        return super.doBeforeRun(client, squad, context);
    }
}
