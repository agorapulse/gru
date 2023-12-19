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

import com.agorapulse.gru.Content;
import com.agorapulse.gru.Gru;
import com.agorapulse.gru.RequestDefinitionBuilder;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@MicronautTest
@Property(name = "gru.http.client", value = "jdk")
public class IdiomaticTest {

    @Inject
    Gru gru;

    @AfterEach
    public void close() {
        gru.close();
    }

    @Test
    public void testHelloCommand() throws Throwable {
        gru.verify(test -> test
            .post("/slack/events", req -> slackEventRequest(req, "command=/hello"))
            .expect(resp -> resp.json("commandHelloResponse.json"))
        );
    }

    private static void slackEventRequest(RequestDefinitionBuilder builder, String requestBody) {
        builder.header("Content-Type", "application/x-www-form-urlencoded")
            .param("query", "queryValue")
            .content(Content.inline(requestBody), "application/x-www-form-urlencoded");
    }

}
