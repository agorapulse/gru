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
import com.agorapulse.gru.DefaultMultipartDefinitionBuilder;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.MultipartDefinitionBuilder;
import com.agorapulse.gru.Squad;

import java.util.function.Consumer;

/**
 * Minion responsible for JSON requests and responses.
 */
public class MultipartMinion extends AbstractMinion<Client> {
    private Consumer<MultipartDefinitionBuilder> multipartDefinition;

    public MultipartMinion() {
        super(Client.class);
    }

    @Override
    public GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (multipartDefinition != null) {
            DefaultMultipartDefinitionBuilder definition = new DefaultMultipartDefinitionBuilder(client);
            multipartDefinition.accept(definition);
            client.getRequest().setMultipart(definition);
        }

        return context;
    }

    public void multipart(Consumer<MultipartDefinitionBuilder> definition) {
        this.multipartDefinition = definition;
    }

    public final int getIndex() {
        return MULTIPART_MINION_INDEX;
    }

}
