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
package com.agorapulse.gru.grails.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.minions.AbstractMinion
import org.springframework.web.servlet.ModelAndView

/**
 * Minion responsible for verifying model returned from the controller action.
 */
class ModelMinion extends AbstractMinion<Grails> {                                      // <1>

    final int index = MODEL_MINION_INDEX                                                // <2>

    Object model                                                                        // <3>

    ModelMinion() {
        super(Grails)                                                                   // <4>
    }

    @Override
    @SuppressWarnings('Instanceof')
    void doVerify(Grails grails, Squad squad, GruContext context) throws Throwable {    // <5>
        if (model instanceof Map && context.result instanceof Map) {
            model.each { key, value ->
                assert context.result[key] == value
            }
        } else if (model instanceof ModelAndView) {
            assert context.result

            ModelAndView result = context.result as ModelAndView

            assert result.model == model.model
            assert result.view == model.view
            assert result.status == model.status
        } else if (model != null) {
            assert context.result == model
        }
    }
}
