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

import com.agorapulse.gru.grails.minions.jsonview.JsonViewSupport
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
                // could be already closed
            }
        then:
            noExceptionThrown()
    }

    void 'some stupid calls'() {
        given:
            ServletContext context = Mock(ServletContext)
        expect:
            !new BootStrap().init(context)
            new VectorInterceptor().after()
            !new BootStrap().destroy()
    }

    void 'json tests are disabled if not present on the classpath'() {
        expect:
            !JsonViewSupport.enabled
    }
}
