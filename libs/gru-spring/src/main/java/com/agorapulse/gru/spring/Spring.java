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
package com.agorapulse.gru.spring;

import com.agorapulse.gru.AbstractClient;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/**
 * Gru implementation based on MockMvc.
 */
public class Spring extends AbstractClient {

    @Deprecated
    public static Spring steal(Object unitTest) {
        return create(unitTest);
    }

    public static Spring create(Object unitTest) {
        return new Spring(unitTest);
    }

    private GruSpringRequest request;
    private GruSpringResponse response;

    private Spring(Object unitTest) {
        super(unitTest);
        reset();
    }

    @Override
    public GruSpringRequest getRequest() {
        return request;
    }

    @Override
    public GruSpringResponse getResponse() {
        return response;
    }

    @Override
    public void reset() {
        request = new  GruSpringRequest();
        response = null;
    }

    @Override
    public GruContext run(Squad squad, GruContext context) {
        MockMvc mockMvc = findMockMvc();
        MockHttpServletRequestBuilder builder = request.getMultipart() != null ? fileUpload(getRequestURI()) : request(request.getMethod(), getRequestURI());

        for (Consumer<MockHttpServletRequestBuilder> step : request.getSteps()) {
            step.accept(builder);
        }

        if (request.getMultipart() != null) {
            MockMultipartHttpServletRequestBuilder upload = (MockMultipartHttpServletRequestBuilder) builder;
            request.getMultipart().getParameters().forEach((k, v) -> {
                builder.param(k, v == null ? null :String.valueOf(v));
            });
            request.getMultipart().getFiles().forEach((k, f) -> {
                upload.file(new MockMultipartFile(
                    f.getParameterName(),
                    f.getFilename(),
                    f.getContentType(),
                    f.getBytes()
                ));
            });
        }

        try {
            MvcResult result = mockMvc.perform(builder).andReturn();
            response = new GruSpringResponse(result.getResponse());
            return context.withResult(result);
        } catch (Exception e) {
            return context.withError(e);
        }
    }

    private MockMvc findMockMvc() {
        if (getUnitTest() instanceof HasMockMvc) {
            HasMockMvc test = (HasMockMvc) getUnitTest();
            return test.getMockMvc();
        }

        System.err.println("Please, let the unit test implement HasMockMvc interface to avoid unnecessary reflection.");

        return Arrays.stream(getUnitTestClass().getDeclaredFields())
            .filter(f -> MockMvc.class.isAssignableFrom(f.getType()))
            .findFirst()
            .map(f -> {
                try {
                    f.setAccessible(true);
                    return (MockMvc) f.get(getUnitTest());
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Cannot read field " + f, e);
                }
            })
            .orElseThrow(() -> new IllegalStateException("MockMvc is missing in the unit test or it is null. Please provide '@Autowired MockMvc mockMvc' field in your specification"));
    }

    private URI getRequestURI() {
        return URI.create(((request.getBaseUri()  == null ? "" : request.getBaseUri()) + (request.getUri() == null ? "" : request.getUri())).replaceAll("/+", "/"));
    }
}
