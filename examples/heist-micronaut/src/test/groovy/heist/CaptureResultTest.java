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

import com.agorapulse.gru.Gru;
import com.agorapulse.gru.minions.AbstractContentMinion;
import com.agorapulse.gru.minions.JsonMinion;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@MicronautTest
public class CaptureResultTest {

    @Inject Gru gru;

    // tag::extractResponseText[]
    @Test
    public void testGet() throws Throwable {
        gru.test(test -> test                                                           // <1>
            .get("/moons/earth/moon")
            .expect(response -> response.json("moon.json"))
        );

        gru.verify();                                                                   // <2>

        String responseText = gru.getSquad()
            .ask(JsonMinion.class, AbstractContentMinion::getResponseText);             // <3>

        Assertions.assertTrue(responseText.contains("moon"));                           // <4>

        gru.close();                                                                    // <5>
    }
    // end::extractResponseText[]

}
