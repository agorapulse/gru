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
package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.okhttp.OkHttp
import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Requires
import spock.lang.Specification

@MicronautTest
@Property(name = 'gru.http.client', value = 'okhttp')
@Requires({ !System.getenv('GRU_HTTP_CLIENT') || System.getenv('GRU_HTTP_CLIENT') == 'okhttp' })
class OkHttpClientSelectionSpec extends Specification {

    @Inject Gru gru

    void 'client is OkHttp based'() {
        expect:
            gru.client.delegate.ensureClient() instanceof OkHttp
    }

}
