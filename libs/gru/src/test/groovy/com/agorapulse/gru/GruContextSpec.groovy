/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
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
package com.agorapulse.gru

import spock.lang.Specification

/**
 * Tests for GruContext.
 */
class GruContextSpec extends Specification {

    private static final String RESULT = 'foo'
    private static final Exception EXCEPTION = new RuntimeException('bar')

    void 'new context from callable - result'() {
        when:
            GruContext context = GruContext.from {
                RESULT
            }
        then:
            !context.hasError()
            context.error == null
            context.hasResult()
            context.result == RESULT
    }

    void 'new context from callable - error'() {
        when:
            GruContext context = GruContext.from {
                throw EXCEPTION
            }
        then:
            !context.hasResult()
            context.result == null
            context.hasError()
            context.hasError(RuntimeException)
            !context.hasError(AssertionError)
            context.error == EXCEPTION
    }

    void 'test create with error'() {
        when:
            GruContext context = GruContext.error(EXCEPTION)
        then:
            context.result == null
            context.error == EXCEPTION
    }

    void 'test create with result'() {
        when:
            GruContext context = GruContext.result(RESULT)
        then:
            context.result == RESULT
            context.error == null
    }

    void 'test create with result and error'() {
        when:
            GruContext context = GruContext.resultAndError(RESULT, EXCEPTION)
        then:
            context.result == RESULT
            context.error == EXCEPTION
    }

    void 'test create with just result'() {
        when:
            GruContext context = GruContext.resultAndError(RESULT, EXCEPTION).justResult()
        then:
            context.result == RESULT
            context.error == null
    }

    void 'test create with just error'() {
        when:
            GruContext context = GruContext.resultAndError(RESULT, EXCEPTION).justError()
        then:
            context.result == null
            context.error == EXCEPTION
    }

    void 'to string'() {
        expect:
            GruContext.EMPTY.toString() == 'Empty GruContext'
            GruContext.error(EXCEPTION).toString() == 'GruContext with error \'java.lang.RuntimeException: bar\''
            GruContext.result(RESULT).toString() == 'GruContext with result \'foo\''
            GruContext.resultAndError(RESULT, EXCEPTION).toString() == 'GruContext with result \'foo\' and error \'java.lang.RuntimeException: bar\''
    }
}
