/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
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
package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.DefaultMultipartDefinitionBuilder
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.MultipartDefinitionBuilder
import com.agorapulse.gru.Squad
import groovy.transform.CompileStatic
import groovy.util.logging.Log

import java.util.function.Consumer

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
class MultipartMinion extends AbstractMinion<Client> {

    final int index = MULTIPART_MINION_INDEX

    private Consumer<MultipartDefinitionBuilder> multipartDefinition

    MultipartMinion() {
        super(Client)
    }

    @Override
    GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (multipartDefinition) {
            DefaultMultipartDefinitionBuilder definition = new DefaultMultipartDefinitionBuilder(client)
            multipartDefinition.accept(definition)
            client.request.multipart = definition
        }
        return context
    }

    void multipart(Consumer<MultipartDefinitionBuilder> definition) {
        this.multipartDefinition = definition
    }
}
