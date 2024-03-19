/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroovyAssertAwareMultipleFailureException extends RuntimeException {

    private final List<Throwable> errors;

    public GroovyAssertAwareMultipleFailureException(List<Throwable> errors) {
        if (errors.isEmpty()) {
            throw new IllegalArgumentException(
                "List of Throwables must not be empty");
        }
        this.errors = new ArrayList<>(errors);
    }

    public List<Throwable> getFailures() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(String.format("There were %d errors:", getFailures().size()));
        for (Throwable e : getFailures()) {
            sb.append(String.format("%n  %s(%s)", e.getClass().getName(), e.toString()));
        }

        return sb.toString();
    }

    @Override
    public void printStackTrace() {
        for (Throwable e: errors) {
            e.printStackTrace();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        for (Throwable e: errors) {
            e.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        for (Throwable e: errors) {
            e.printStackTrace(s);
        }
    }

}
