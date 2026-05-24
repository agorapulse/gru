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

import org.springframework.http.HttpMethod

class ApiUrlMappings {

    static mappings = {
        "/api/v1/moons/$planet"(controller: "moon", action: "all", method: HttpMethod.GET)
        "/api/v1/moons/$planet"(controller: "moon", action: "create", method: HttpMethod.POST)
        "/api/v1/moons/$planet/$moon"(controller: "moon", action: "moon", method: HttpMethod.GET)
        "/api/v1/moons/$planet/$moon"(controller: "moon", action: "steal", method: HttpMethod.DELETE)
    }
}
