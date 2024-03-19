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
package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Content;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.content.ContentUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Minion responsible for JSON requests and responses.
 */
public class PayloadMinion extends AbstractMinion<Client> {

    private Content payload;
    private String contentType;
    public PayloadMinion() {
        super(Client.class);
    }

    @Override
    public GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (payload != null) {
            InputStream inputStream = payload.load(client);

            if (inputStream == null && payload.isSaveSupported()) {
                try {
                    payload.save(client, new ByteArrayInputStream(new byte[0]));
                    System.err.println("Content missing for " + payload + ". Content was saved.");
                    inputStream = new ByteArrayInputStream(new byte[0]);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Cannot save content " + payload, e);
                }
            }

            client.getRequest().setContent(contentType, ContentUtils.getBytes(inputStream));
        }

        return context;
    }

    public final int getIndex() {
        return CONTENT_MINION_INDEX;
    }

    public void setPayload(Content payload) {
        this.payload = payload;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
