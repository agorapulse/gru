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
package heist;

import com.agorapulse.gru.Gru;
import com.agorapulse.gru.micronaut.Micronaut;
import io.micronaut.context.env.Environment;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutomaticMicronautTest {

    Gru gru = Gru.create(Micronaut.create(this));                                       // <1>

    @Inject Environment environment;                                                    // <2>

    @Test
    public void testAutomaticContext() throws Throwable {
        assertNotNull(environment);
        assertTrue(environment.getActiveNames().contains("test"));

        gru.verify(test -> test
            .get("/moons/earth/moon")
            .expect(response -> response.json("moon.json"))
        );
    }

}
