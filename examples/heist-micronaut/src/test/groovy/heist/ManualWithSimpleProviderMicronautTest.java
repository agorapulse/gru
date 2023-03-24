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
package heist;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Gru;
import com.agorapulse.gru.micronaut.Micronaut;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ManualWithSimpleProviderMicronautTest {

    private Gru gru = Gru.create(Micronaut.create(this, this::getContext));             // <1>

    private ApplicationContext context;
    private EmbeddedServer embeddedServer;

    @BeforeEach
    public void setup() {
        context = ApplicationContext.builder().build();
        context.start();

        embeddedServer = context.getBean(EmbeddedServer.class);
        embeddedServer.start();
    }

    @AfterEach
    public void cleanUp() {
        embeddedServer.close();
        context.close();
    }


    @Test
    public void testMoon() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth/moon")
            .expect(response -> response.json("moon.json"))
        );
    }

    private ApplicationContext getContext() {
        return context;
    }

}
