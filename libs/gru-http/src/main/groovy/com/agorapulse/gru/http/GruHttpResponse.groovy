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
package com.agorapulse.gru.http

import com.agorapulse.gru.Client
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import okhttp3.Response

/**
 * Wrapper around OkHttp response.
 */
@CompileStatic
@PackageScope
class GruHttpResponse implements Client.Response {

    private final Response response

    GruHttpResponse(Response response) {
        this.response = response
    }

    @Override
    int getStatus() {
        if (response.priorResponse()?.redirect) {
            return response.priorResponse().code()
        }
        return response.code()
    }

    @Override
    List<String> getHeaders(String name) {
        return response.headers(name)
    }

    @Override
    String getText() {
        return response.body().string()
    }

    @Override
    String getRedirectUrl() {
        return response.priorResponse()?.header('Location')
    }

}
