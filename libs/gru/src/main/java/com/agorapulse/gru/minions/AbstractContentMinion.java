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
package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Content;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.content.ContentUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Minion responsible for JSON requests and responses.
 */
public abstract class AbstractContentMinion<C extends Client> extends AbstractMinion<C> {


    public static final String TEST_RESOURCES_FOLDER_PROPERTY_NAME = "TEST_RESOURCES_FOLDER";
    public static final String ENVIRONMENT_VARIABLE_REWRITE_RESOURCES = "COM_AGORAPULSE_GRU_REWRITE";
    public static final String SYSTEM_PROPERTY_REWRITE_RESOURCES = "com.agorapulse.gru.rewrite";
    private Content responseContent;
    private String responseText;
    private final boolean rewrite;
    protected final List<Content> createdResources = new ArrayList<>();

    protected AbstractContentMinion(Class<C> clientType) {
        super(clientType);
        rewrite = Boolean.TRUE.toString().equals(System.getenv(ENVIRONMENT_VARIABLE_REWRITE_RESOURCES)) || Boolean.TRUE.toString().equals(System.getProperty(SYSTEM_PROPERTY_REWRITE_RESOURCES));
    }

    @Override
    public final void doVerify(Client client, Squad squad, GruContext context) throws Throwable {
        if (responseContent != null) {
            final InputStream is = responseContent.load(client);
            String expectedResponseText = is == null ? null : ContentUtils.getText(is);
            responseText = readResponseText(client, squad, context);

            if (expectedResponseText == null && responseText != null) {
                expectedResponseText = responseText;
                if (responseContent.isSaveSupported()) {
                    responseContent.save(client, new ByteArrayInputStream(normalize(expectedResponseText).getBytes()));
                    System.err.println("Content is missing for " + getResponseContent() + ". New file was created with content:\n" + expectedResponseText);
                    createdResources.add(responseContent);
                }
            }

            if (expectedResponseText != null) {
                try {
                    similar(normalize(responseText), normalize(expectedResponseText));
                } catch (AssertionError assertionError) {
                    if (rewrite && isRewriteSupported() && responseContent.isSaveSupported()) {
                        rewrite(client, normalize(responseText), normalize(expectedResponseText));
                    } else {
                        throw assertionError;
                    }
                }
            }
        }


        if (!createdResources.isEmpty()) {
            throw new AssertionError("Fixture files were created or updated: " + createdResources.stream().map(Object::toString).collect(Collectors.joining(", ")) + ". Please, run the test again to verify it is repeatable.");
        }

    }

    public Content getResponseContent() {
        return responseContent;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseContent(Content responseContent) {
        this.responseContent = responseContent;
    }

    protected abstract void similar(String actual, String expected) throws AssertionError;

    protected String normalize(String input) {
        return input;
    }

    protected boolean isRewriteSupported() {
        return false;
    }

    protected String rewrite(Client client, String actualText, String expectedText) {
        throw new UnsupportedOperationException("Rewrite is not supported");
    }

    protected String readResponseText(Client client, Squad squad, GruContext context) {
        return client.getResponse().getText();
    }

}
