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
package com.agorapulse.gru.exception;

import org.junit.runners.model.MultipleFailureException;

import java.util.List;

public class GroovyAssertAwareMultipleFailureException extends MultipleFailureException {

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(String.format("There were %d errors:", getFailures().size()));
        for (Throwable e : getFailures()) {
            sb.append(String.format("\n  %s(%s)", e.getClass().getName(), e.toString()));
        }

        return sb.toString();
    }

    public GroovyAssertAwareMultipleFailureException(List<Throwable> errors) {
        super(errors);
    }

}
