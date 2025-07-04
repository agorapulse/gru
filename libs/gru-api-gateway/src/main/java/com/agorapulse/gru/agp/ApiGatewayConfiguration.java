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
package com.agorapulse.gru.agp;

import com.agorapulse.gru.HttpVerbsShortcuts;
import com.agorapulse.gru.agp.ignore.Safe;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;

public class ApiGatewayConfiguration implements HttpVerbsShortcuts {

    public static class Mapping {

        private final Route route;
        private final String handler;
        private final boolean proxied;
        private final MappingConfiguration configuration = new MappingConfiguration();

        Mapping(Route route, String handler, boolean proxied) {
            this.route = route;
            this.handler = handler;
            this.proxied = proxied;
        }

        @SuppressWarnings("unchecked")
        String executeHandler(ApiGatewayProxyRequest request, Object unitTest) throws ClassNotFoundException, IOException, IllegalAccessException, InvocationTargetException, InstantiationException {
            String[] parts = handler.split("::");
            String className = parts[0];
            String methodName = parts[1];

            Class<?> clazz = Class.forName(className);

            if (RequestStreamHandler.class.isAssignableFrom(clazz)) {
                return handleRequestStreamHandler(request, unitTest, (Class<? extends RequestStreamHandler>) clazz);
            }

            Method method = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getGenericParameterTypes().length == 2 && Context.class.equals(m.getGenericParameterTypes()[1]))
                .max(comparing((Method m) -> distanceToObject(m.getParameterTypes()[0])))
                .orElseThrow(() -> new IllegalArgumentException("Cannot find method " + methodName + " in " + className + " matching the signature contract"));

            Type firstParameterType = method.getGenericParameterTypes()[0];

            Class<?> firstParameter = null;

            if (firstParameterType instanceof Class) {
                firstParameter = (Class<?>) firstParameterType;
            } else if (firstParameterType instanceof TypeVariable) {
                TypeVariable<?> variable = (TypeVariable<?>) firstParameterType;
                int indexOfTypeVariable = Arrays.asList(clazz.getSuperclass().getTypeParameters()).indexOf(variable);
                if (indexOfTypeVariable >= 0) {
                    Type genericSuperclass = clazz.getGenericSuperclass();
                    if (genericSuperclass instanceof ParameterizedType) {
                        Type actualTypeArgument = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[indexOfTypeVariable];
                        if (actualTypeArgument instanceof Class) {
                            firstParameter = (Class<?>) actualTypeArgument;
                        }
                    }
                }
            }

            if (firstParameter == null) {
                throw new IllegalArgumentException("Cannot determine type of the first parameter of method " + methodName);
            }

            ObjectMapper mapper = new ObjectMapper();

            // TODO: handle streaming handler

            request.setPathParameters(route.extractPathParameters(request.getPath()));

            Object input = mapper.readValue(prepareRequestObject(request), firstParameter);

            Object result = method.invoke(getOrCreateHandler(unitTest, clazz), input, request.getContext());

            OutputStream outputStream = new ByteArrayOutputStream();
            mapper.writeValue(outputStream, prepareResponseObject(result));
            return outputStream.toString();
        }

        private String handleRequestStreamHandler(ApiGatewayProxyRequest request, Object unitTest, Class<? extends RequestStreamHandler> handlerClass) throws IllegalAccessException, InstantiationException, IOException {
            RequestStreamHandler streamHandler = getOrCreateHandler(unitTest, handlerClass);

            ByteArrayInputStream bais = new ByteArrayInputStream(prepareRequestObject(request).getBytes());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            streamHandler.handleRequest(bais, baos, request.getContext());

            baos.flush();

            return baos.toString();
        }

        private String prepareRequestObject(ApiGatewayProxyRequest request) {
            if (proxied) {
                return request.toJson();
            }

            return request.toJson(configuration);
        }


        private Object prepareResponseObject(Object result) {
            if (proxied) {
                return result;
            }
            ResponseMappingConfiguration responseMappingConfiguration = configuration.getResponse(200);

            return responseMappingConfiguration.wrapResult(result);
        }

        private int distanceToObject(Class<?> clazz) {
            return distanceToObject(clazz, 0);
        }

        private int distanceToObject(Class<?> clazz, int initial) {
            if (Object.class.equals(clazz) || clazz == null) {
                return initial;
            }
            return distanceToObject(clazz.getSuperclass(), initial + 1);
        }

        private <T> T getOrCreateHandler(Object unitTest, Class<T> clazz) {
            return Arrays.stream(unitTest.getClass().getDeclaredFields())
                .filter(f -> clazz.equals(f.getType()))
                .findFirst()
                .map(f -> getFieldValue(unitTest, clazz, f))
                .orElseGet(() -> createNewInstance(clazz));
        }

        private void configure(Consumer<MappingConfiguration> configuration) {
            configuration.accept(this.configuration);
        }

        private static <T> T createNewInstance(Class<T> clazz) {
            try {
                return clazz.newInstance();
            } catch (SecurityException | InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        private static <T> T getFieldValue(Object unitTest, Class<T> clazz, Field f) {
            return Safe.call(() -> {
                f.setAccessible(true);
                return clazz.cast(f.get(unitTest));
            });
        }
    }

    public static class MappingConfiguration {
        private final Set<String> queryStringParameters = new LinkedHashSet<>();
        private final Set<String> pathParameters = new LinkedHashSet<>();
        private final Map<Integer, ResponseMappingConfiguration> responses = new LinkedHashMap<>();


        public MappingConfiguration queryStringParameters(String first, String... rest) {
            queryStringParameters.add(first);
            if (rest.length > 0) {
                queryStringParameters.addAll(Arrays.asList(rest));
            }
            return this;
        }

        public MappingConfiguration pathParameters(String first, String... rest) {
            pathParameters.add(first);
            if (rest.length > 0) {
                pathParameters.addAll(Arrays.asList(rest));
            }
            return this;
        }

        public MappingConfiguration response(int number, Consumer<ResponseMappingConfiguration> configuration) {
            responses.put(number, ResponseMappingConfiguration.from(configuration));
            return this;
        }

        Set<String> getQueryStringParameters() {
            return Collections.unmodifiableSet(queryStringParameters);
        }

        Set<String> getPathParameters() {
            return Collections.unmodifiableSet(pathParameters);
        }

        ResponseMappingConfiguration getResponse(int code) {
            return responses.computeIfAbsent(code, status -> new ResponseMappingConfiguration());
        }
    }

    public static class ResponseMappingConfiguration {

        private final Map<String, String> headers = new LinkedHashMap<>();

        private static ResponseMappingConfiguration from(Consumer<ResponseMappingConfiguration> configuration) {
            ResponseMappingConfiguration response = new ResponseMappingConfiguration();
            configuration.accept(response);
            return response;
        }

        public ResponseMappingConfiguration headers(Map<String, Object> additionalHeaders) {
            additionalHeaders.forEach((key, value) -> headers.put(key, String.valueOf(value)));
            return this;
        }

        Object wrapResult(Object result) {
            Map<String, Object> jsonResponse = new LinkedHashMap<>();

            jsonResponse.put("body", result);

            if (!headers.isEmpty()) {
                jsonResponse.put("headers", headers);
            }

            return jsonResponse;
        }
    }

    public static class Route {

        private final ApiGatewayConfiguration self;
        private final Pattern url;
        private final Set<String> pathParametersNames;
        private final Set<String> methods;

        Route(ApiGatewayConfiguration self, String url, Collection<String> methods) {
            this.self = self;
            this.url = createPatternForUrlMapping(url);
            this.pathParametersNames = createNamedParametersNames(url);
            this.methods = new LinkedHashSet<>(methods);
        }

        public Mapping to(Class<?> handler) {
            return to(handler.getName());
        }

        public Mapping to(Class<?> handler, Consumer<MappingConfiguration> configuration) {
            return to(handler.getName(), configuration);
        }

        public Mapping to(String handler) {
            return to(handler, c -> {
            }, true);
        }

        public Mapping to(String handler, Consumer<MappingConfiguration> configuration) {
            return to(handler, configuration, false);
        }

        public Mapping to(String handler, Consumer<MappingConfiguration> configuration, boolean proxied) {
            Mapping mapping = new Mapping(this, handler.contains("::") ? handler : (handler + "::handleRequest"), proxied);
            Optional.ofNullable(configuration).ifPresent(mapping::configure);
            self.mappings.add(mapping);
            return mapping;
        }

        boolean matches(String method, String url) {
            return methods.contains(method) && this.url.matcher(url).matches();
        }

        Map<String, String> extractPathParameters(String path) {
            Matcher matcher = this.url.matcher(path);

            if (!matcher.matches()) {
                throw new IllegalStateException("Path " + path + " does not match!");
            }

            Map<String, String> ret = new LinkedHashMap<>();

            for (String pathParameter : pathParametersNames) {
                ret.put(pathParameter, matcher.group(pathParameter));
            }

            return ret;
        }

        private Pattern createPatternForUrlMapping(String url) {
            if (url.contains("{")) {
                return Pattern.compile(url.replaceAll("\\{([^}]+)}", "(?<$1>.*?)"));
            }
            return Pattern.compile(Pattern.quote(url));
        }

        private Set<String> createNamedParametersNames(String url) {
            if (url.contains("{")) {
                Set<String> ret = new LinkedHashSet<>();
                Matcher matcher = Pattern.compile("\\{([^}]+)}").matcher(url);
                while (matcher.find()) {
                    ret.add(matcher.group(1));
                }
                return ret;
            }
            return Collections.emptySet();
        }
    }

    private final List<Mapping> mappings = new ArrayList<>();

    public Route map(String url, String... httpMethods) {
        Collection<String> methods;

        if (httpMethods.length == 0) {
            methods = Arrays.asList(HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, GET);
        } else {
            methods = Arrays.asList(httpMethods);
        }

        return new Route(this, url, methods);
    }

    Mapping findMapping(String method, String url) {
        for (Mapping mapping : mappings) {
            if (mapping.route.matches(method, url)) {
                return mapping;
            }
        }
        throw new IllegalArgumentException(String.format("Route %s: %s is not mapped!", method, url));
    }
}
