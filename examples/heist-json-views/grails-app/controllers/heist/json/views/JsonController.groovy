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
package heist.json.views

import grails.converters.JSON

class JsonController {

    def index() {
        response.status = 203
        response.setHeader('foo', 'bar')
        render(contentType: 'x-application/moons', text: ([[name: 'Moon', planet: 'Earth']] as JSON).toString())
    }

    def show() {
        if (params.manual) {
            render([name: 'Moon', planet: 'Earth'] as JSON)
            return
        }
        response.status = 200
        render([name: 'Moon', planet: 'Earth'] as JSON)
    }

    def missing() {
        // This should try to render a view that doesn't exist
        render(view: 'nonExistentView', model: [moon: [name: 'Moon', planet: 'Earth']])
    }

}
