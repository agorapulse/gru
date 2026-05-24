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
import grails.core.GrailsControllerClass;
import grails.testing.web.controllers.ControllerUnitTest;
import grails.util.GrailsNameUtils;
import grails.util.GrailsStringUtils;
import grails.web.mapping.UrlMapping;
import grails.web.mapping.UrlMappingInfo;
import grails.web.mapping.UrlMappingsHolder;
import javassist.util.proxy.ProxyObject;
import org.codehaus.groovy.runtime.MethodClosure;
import org.grails.core.artefact.ControllerArtefactHandler;
import org.grails.core.artefact.UrlMappingsArtefactHandler;
import org.grails.web.mapping.UrlMappingsHolderFactoryBean;
import org.grails.web.mapping.mvc.GrailsControllerUrlMappings;
import org.grails.web.servlet.mvc.GrailsWebRequest;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlMappingsMinion extends AbstractMinion<Grails> {

    private static final String URL_MAPPINGS_HOLDER_BEAN = "grailsUrlMappingsHolder";
    private static final String DEFAULT_URL_MAPPINGS_CLASS = "UrlMappings";

    private UrlMappingInfo urlMappingInfo;
    private GrailsControllerUrlMappings urlMappingsHolder;

    private List<Class<?>> urlMappings = new ArrayList<>();
    private MethodClosure action;

    public UrlMappingsMinion() {
        super(Grails.class);
    }

    @Override
    public int getIndex() {
        return URL_MAPPINGS_MINION_INDEX;
    }

    public List<Class<?>> getUrlMappings() {
        return urlMappings;
    }

    public void setUrlMappings(List<Class<?>> urlMappings) {
        this.urlMappings = urlMappings;
    }

    public MethodClosure getAction() {
        return action;
    }

    public void setAction(MethodClosure action) {
        this.action = action;
    }

    @Override
    protected GruContext doBeforeRun(Grails grails, Squad squad, GruContext context) {
        if (urlMappings.isEmpty() && grails.getRequest().getUri() != null) {
            try {
                urlMappings.add(getClass().getClassLoader().loadClass(DEFAULT_URL_MAPPINGS_CLASS));
            } catch (ClassNotFoundException ignored) {
                return context.withError(new AssertionError(
                    "URI for action is specified but UrlMappings is not defined nor default UrlMappings class exists!"
                ));
            }
        }

        try {
            ControllerUnitTest<?> test = grails.getUnitTest();
            UrlMappingInfo info = readMappingInfo(test);
            @SuppressWarnings("unchecked")
            Map<String, Object> params = info.getParameters();
            test.getParams().putAll(params);

            Object idValue = params.get(GrailsWebRequest.ID_PARAMETER);
            String id = idValue == null ? null : idValue.toString();
            if (!GrailsStringUtils.isBlank(id)) {
                try {
                    test.getParams().put(GrailsWebRequest.ID_PARAMETER, URLDecoder.decode(id, StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException e) {
                    test.getParams().put(GrailsWebRequest.ID_PARAMETER, id);
                }
            }

            return context;
        } catch (AssertionError ae) {
            return context.withError(ae);
        }
    }

    public GrailsControllerUrlMappings getUrlMappingsHolder(ControllerUnitTest<?> unitTest) {
        if (urlMappingsHolder != null) {
            return urlMappingsHolder;
        }
        initUrlMappingsArtifacts(unitTest);
        defineMappingsHolder(unitTest);
        urlMappingsHolder = unitTest.getApplicationContext().getBean(URL_MAPPINGS_HOLDER_BEAN, GrailsControllerUrlMappings.class);
        return urlMappingsHolder;
    }

    public GrailsControllerClass getControllerClass(ControllerUnitTest<?> unitTest) {
        return (GrailsControllerClass) unitTest.getGrailsApplication().getArtefactByLogicalPropertyName(
            ControllerArtefactHandler.TYPE,
            getControllerName(unitTest)
        );
    }

    public String getActionName(ControllerUnitTest<?> unitTest) {
        if (action != null) {
            return action.getMethod();
        }
        UrlMappingInfo info = readMappingInfo(unitTest);
        String actionName = info.getActionName();
        return actionName != null ? actionName : getControllerClass(unitTest).getDefaultAction();
    }

    public String getControllerName(ControllerUnitTest<?> unitTest) {
        if (action != null) {
            Class<?> controllerType = action.getOwner().getClass();
            if (action.getOwner() instanceof ProxyObject || controllerType.getSimpleName().contains("$")) {
                controllerType = controllerType.getSuperclass();
            }
            return GrailsNameUtils.getPropertyName(GrailsNameUtils.getLogicalName(controllerType, "Controller"));
        }
        UrlMappingInfo info = readMappingInfo(unitTest);
        return info.getControllerName();
    }

    private void initUrlMappingsArtifacts(ControllerUnitTest<?> unitTest) {
        for (Class<?> urlMappingClass : urlMappings) {
            unitTest.getGrailsApplication().addArtefact(UrlMappingsArtefactHandler.TYPE, urlMappingClass);
        }
    }

    private void defineMappingsHolder(ControllerUnitTest<?> unitTest) {
        ConfigurableApplicationContext ctx = unitTest.getApplicationContext();
        if (!(ctx instanceof BeanDefinitionRegistry)) {
            throw new IllegalStateException("Application context is not a BeanDefinitionRegistry: " + ctx);
        }
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ctx;
        if (registry.containsBeanDefinition(URL_MAPPINGS_HOLDER_BEAN)) {
            return;
        }
        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(UrlMappingsHolderFactoryBean.class);
        def.getPropertyValues().add("grailsApplication", unitTest.getGrailsApplication());
        registry.registerBeanDefinition(URL_MAPPINGS_HOLDER_BEAN, def);
    }

    @SuppressWarnings("unchecked")
    private UrlMappingInfo readMappingInfo(ControllerUnitTest<?> unitTest) {
        if (urlMappingInfo != null) {
            return urlMappingInfo;
        }

        UrlMappingsHolder mappingsHolder = getUrlMappingsHolder(unitTest);
        String requestURI = unitTest.getRequest().getRequestURI();
        String httpMethod = unitTest.getRequest().getMethod();
        if (httpMethod == null) {
            httpMethod = UrlMapping.ANY_HTTP_METHOD;
        }

        UrlMappingInfo[] matches = mappingsHolder.matchAll(requestURI, httpMethod);
        List<UrlMappingInfo> mappingInfos = new ArrayList<>();
        if (matches != null) {
            for (UrlMappingInfo i : matches) {
                mappingInfos.add(i);
            }
        }

        if (mappingInfos.isEmpty()) {
            throw new AssertionError("URL '" + requestURI + "' is not mapped with method " + unitTest.getRequest().getMethod() + "!");
        }

        if (action == null) {
            urlMappingInfo = mappingInfos.get(0);
            return urlMappingInfo;
        }

        GrailsControllerClass controllerClass = getControllerClass(unitTest);
        for (UrlMappingInfo mapping : mappingInfos) {
            mapping.configure(unitTest.getWebRequest());
            String mappingAction = mapping.getActionName();
            if (mappingAction == null) {
                mappingAction = controllerClass.getDefaultAction();
            }
            if (getControllerName(unitTest).equals(mapping.getControllerName())
                && getActionName(unitTest).equals(mappingAction)) {
                urlMappingInfo = mapping;
                return urlMappingInfo;
            }
        }

        throw new AssertionError(unitTest.getRequest().getMethod() + ": '" + requestURI
            + "' is not mapped to " + action.getOwner().getClass().getName() + "." + action.getMethod() + "!");
    }
}
