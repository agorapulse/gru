/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2026 Agorapulse.
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
package com.agorapulse.gru.grails.minions;

import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.grails.Grails;
import com.agorapulse.gru.minions.AbstractMinion;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Objects;

/**
 * Minion responsible for verifying model returned from the controller action.
 */
public class ModelMinion extends AbstractMinion<Grails> {

    private Object model;

    public ModelMinion() {
        super(Grails.class);
    }

    @Override
    public int getIndex() {
        return MODEL_MINION_INDEX;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    @Override
    protected void doVerify(Grails grails, Squad squad, GruContext context) {
        if (model == null) {
            return;
        }
        Object result = context.getResult();

        if (model instanceof Map<?, ?> && result instanceof Map<?, ?>) {
            Map<?, ?> expectedMap = (Map<?, ?>) model;
            Map<?, ?> resultMap = (Map<?, ?>) result;
            for (Map.Entry<?, ?> entry : expectedMap.entrySet()) {
                Object actualValue = resultMap.get(entry.getKey());
                if (!Objects.equals(actualValue, entry.getValue())) {
                    throw new AssertionError("Model entry '" + entry.getKey() + "' was '" + actualValue + "' but expected '" + entry.getValue() + "'");
                }
            }
            return;
        }

        if (model instanceof ModelAndView) {
            if (!(result instanceof ModelAndView)) {
                throw new AssertionError("Controller did not return a ModelAndView (got: " + result + ")");
            }
            ModelAndView expected = (ModelAndView) model;
            ModelAndView actual = (ModelAndView) result;
            if (!Objects.equals(actual.getModel(), expected.getModel())) {
                throw new AssertionError("ModelAndView model differs - expected " + expected.getModel() + " but was " + actual.getModel());
            }
            if (!Objects.equals(actual.getViewName(), expected.getViewName())) {
                throw new AssertionError("ModelAndView view differs - expected " + expected.getViewName() + " but was " + actual.getViewName());
            }
            if (actual.getStatus() != expected.getStatus()) {
                throw new AssertionError("ModelAndView status differs - expected " + expected.getStatus() + " but was " + actual.getStatus());
            }
            return;
        }

        if (!Objects.equals(result, model)) {
            throw new AssertionError("Controller result '" + result + "' does not equal expected model '" + model + "'");
        }
    }
}
