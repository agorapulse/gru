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
package heist;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Gru;
import com.agorapulse.gru.micronaut.Micronaut;
import heist.micronaut.Moon;
import heist.micronaut.MoonService;
import io.micronaut.context.env.Environment;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AdvancedAutomaticMicronautTest {

    private MoonService moonService = mock(MoonService.class);                          // <1>

    private Gru gru = Gru.create(
        Micronaut.build(this)
            .doWithContextBuilder(b ->
                b.environments("my-custom-env")                                         // <2>
            ).doWithContext(c ->
                c.registerSingleton(MoonService.class, moonService)                     // <3>
            ).start()                                                                   // <4>
    );

    @Inject
    private Environment environment;                                                    // <5>

    @Test
    public void testMoon() throws Throwable {
        assertNotNull(environment);

        assertTrue(environment.getActiveNames().contains("test"));
        assertTrue(environment.getActiveNames().contains("my-custom-env"));

        when(moonService.get("earth", "moon")).thenReturn(new Moon("earth", "moon"));

        gru.verify(test -> test
            .get("/moons/earth/moon")
            .expect(response -> response.json("moon.json"))
        );

        verify(moonService, times(1)).get("earth", "moon");
    }

}
