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
package com.agorapulse.gru;

import java.util.concurrent.Callable;

/**
 * Gru context holds result of the controller's method call and any exception thrown during setup or action execution.
 */
public class GruContext {

    /**
     * Empty context instance.
     */
    public static final GruContext EMPTY = new GruContext(null, null);

    /**
     * Creates new context from callable, storing the result as result and any exception thrown as error.
     * @param code code returning the result and throwing any exception which will be assigned to error property
     * @return new context from callable, storing the result as result and any exception thrown as error
     */
    public static GruContext from(Callable<?> code) {
        Object result = null;
        Throwable error = null;
        try {
            result = code.call();
        } catch (Throwable e) {
            error = e;
        }
        return new GruContext(result, error);
    }

    /**
     * Creates new context with just error.
     * @param error error of newly created context
     * @return new context with just error
     */
    public static GruContext error(Throwable error) {
        return new GruContext(null, error);
    }

    /**
     * Creates new context with just result.
     * @param result result of newly created context
     * @return new context with just result
     */
    public static GruContext result(Object result) {
        return new GruContext(result, null);
    }

    /**
     * Creates new context holding given result and error.
     * @param result result of newly created context
     * @param error error of newly created context
     * @return new context holding given result and error
     */
    public static GruContext resultAndError(Object result, Throwable error) {
        return new GruContext(result, error);
    }

    private final Object result;
    private final Throwable error;

    private GruContext(Object result, Throwable error) {
        this.result = result;
        this.error = error;
    }

    /**
     * @return the result of execution
     */
    public Object getResult() {
        return result;
    }

    /**
     * @return true if there is any result of execution
     */
    public boolean hasResult() {
        return result != null;
    }

    /**
     * @return new context holding just the result of execution, ignoring the error
     */
    public GruContext justResult() {
        return new GruContext(result, null);
    }

    /**
     * @param result new result
     * @return new context with modified result
     */
    public GruContext withResult(Object result) {
        return new GruContext(result, error);
    }

    /**
     * @return the error thrown during the execution
     */
    public Throwable getError() {
        return error;
    }

    /**
     * @return new context holding just the error of execution, ignoring the result
     */
    public GruContext justError() {
        return new GruContext(null, error);
    }

    /**
     * @param error new error
     * @return new context with modified error
     */
    public GruContext withError(Throwable error) {
        return new GruContext(result, error);
    }

    /**
     * @throws AssertionError if error is present
     */
    public void throwErrorIfPresent() throws AssertionError {
        if (hasError(AssertionError.class)) {
            throw (AssertionError) error;
        }
        if (hasError()) {
            throw new AssertionError(error);
        }
    }

    /**
     * @return true if there was any error during the execution
     */
    public boolean hasError() {
        return error != null;
    }

    /**
     * @return true if there was any error of given type during the execution
     */
    public <T extends Throwable> boolean hasError(Class<T> type) {
        return type.isInstance(error);
    }

    /**
     * Cleans the error of given type if present.
     * @param type type of the error to be cleaned
     * @param <T> type of the error to be cleaned
     * @return new context without error of given type
     */
    public <T extends Throwable> GruContext cleanError(Class<T> type) {
        if (hasError(type)) {
            return withError(null);
        }
        return this;
    }

    @Override
    public String toString() {
        if (result == null && error == null) {
            return "Empty GruContext";
        }

        StringBuilder builder = new StringBuilder("GruContext with ");

        if (result != null) {
            builder.append("result '").append(result).append("'");
            if (error != null) {
                builder.append(" and ");
            }
        }

        if (error != null) {
            builder.append("error '").append(error).append("'");
        }

        return builder.toString();
    }
}
