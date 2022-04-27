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
package heist

class MoonService {

    private Map<String, Map> moons = ['earth:moon': [name: 'Moon', planet: 'Earth', created: new Date()]]

    Map findByName(String name) {
        return moons.find {it.key.endsWith(":${name.toLowerCase()}")}?.value
    }

    List<Map> findByPlanet(String planet) {
        return moons.findAll { it.key.startsWith(planet.toLowerCase() + ":") }.values().toList()
    }

    Map findByPlanetAndName(String planet, String name) {
        return moons["$planet:$name".toString().toLowerCase()]
    }

    Map createMoonForPlanet(String planet, Map moon) {
        Map newMoon = new LinkedHashMap(moon)
        newMoon.created = new Date()
        moons["$planet:$moon.name".toString().toLowerCase()] = newMoon
        return moon
    }

}
