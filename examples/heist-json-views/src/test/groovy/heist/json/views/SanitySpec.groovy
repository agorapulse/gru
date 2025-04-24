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

import grails.boot.GrailsApp
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

import javax.servlet.ServletContext

/**
 * Trying to test untestable.
 */
class SanitySpec extends Specification {

    @ConfineMetaClassChanges(GrailsApp)
    void 'mock run app'() {
        when:
            Application.main()
        then:
            Application.context
        when:
            try {
                Application.context.stop()
            } catch (IllegalStateException ignored) {
                // application is already stopping
            }
        then:
            noExceptionThrown()
    }

    void 'some stupid calls'() {
        given:
            ServletContext context = Mock(ServletContext)
        expect:
            !new BootStrap().init(context)
            !new BootStrap().destroy()
    }
}
