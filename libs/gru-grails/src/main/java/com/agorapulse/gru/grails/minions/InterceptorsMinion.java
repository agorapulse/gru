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
import grails.artefact.Interceptor;
import grails.core.GrailsClass;
import grails.testing.web.controllers.ControllerUnitTest;
import org.grails.plugins.web.interceptors.GrailsInterceptorHandlerInterceptorAdapter;
import org.grails.plugins.web.interceptors.InterceptorArtefactHandler;
import org.grails.web.util.GrailsApplicationAttributes;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterceptorsMinion extends AbstractMinion<Grails> {

    private static final String INTERCEPTOR_ADAPTER_BEAN = "grailsInterceptorHandlerInterceptorAdapter";

    private List<Class<?>> interceptors = new ArrayList<>();
    private List<Class<?>> autowired = new ArrayList<>();
    private GrailsInterceptorHandlerInterceptorAdapter interceptorAdapter;

    public InterceptorsMinion() {
        super(Grails.class);
    }

    @Override
    public int getIndex() {
        return INTERCEPTORS_MINION_INDEX;
    }

    public List<Class<?>> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Class<?>> interceptors) {
        this.interceptors = interceptors;
    }

    public List<Class<?>> getAutowired() {
        return autowired;
    }

    public void setAutowired(List<Class<?>> autowired) {
        this.autowired = autowired;
    }

    @Override
    protected GruContext doBeforeRun(Grails grails, Squad squad, GruContext context) {
        if (interceptors.isEmpty()) {
            return context;
        }

        ControllerUnitTest<?> test = grails.getUnitTest();
        mockInterceptorAdapter(test);
        for (Class<?> interceptorClass : interceptors) {
            mockInterceptor(test, interceptorClass);
        }

        interceptorAdapter = test.getApplicationContext().getBean(GrailsInterceptorHandlerInterceptorAdapter.class);
        Collection<Interceptor> beans = test.getApplicationContext().getBeansOfType(Interceptor.class, true, false).values();
        interceptorAdapter.setInterceptors(beans.toArray(new Interceptor[0]));

        try {
            if (!interceptorAdapter.preHandle(test.getRequest(), test.getResponse(), null)) {
                return context.withError(new NotHandled());
            }
        } catch (Exception e) {
            return context.withError(e);
        }
        return context;
    }

    @Override
    protected GruContext doAfterRun(Grails grails, Squad squad, GruContext ctx) {
        if (interceptorAdapter == null) {
            return ctx;
        }

        ControllerUnitTest<?> test = grails.getUnitTest();

        boolean preHandled = !ctx.hasError(NotHandled.class);
        GruContext context = ctx.cleanError(NotHandled.class);
        if (context.hasError(Exception.class)) {
            return afterCompletionWithError(test, context, (Exception) context.getError());
        }

        try {
            if (preHandled) {
                ModelAndView modelAndView = null;
                Object attr = test.getRequest().getAttribute(GrailsApplicationAttributes.MODEL_AND_VIEW);
                if (attr instanceof ModelAndView) {
                    modelAndView = (ModelAndView) attr;
                } else if (context.getResult() instanceof Map) {
                    String actionName = squad.ask(UrlMappingsMinion.class, m -> m.getActionName(test));
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resultMap = (Map<String, Object>) context.getResult();
                    modelAndView = new ModelAndView(actionName, new HashMap<>(resultMap));
                } else if (context.getResult() instanceof ModelAndView) {
                    modelAndView = (ModelAndView) context.getResult();
                }
                interceptorAdapter.postHandle(test.getRequest(), test.getResponse(), this, modelAndView);
            }
            return context;
        } catch (Exception e) {
            return afterCompletionWithError(test, context, e);
        }
    }

    @Override
    protected void doVerify(Grails grails, Squad squad, GruContext resultAndError) {
        if (interceptors.isEmpty()) {
            return;
        }
        ControllerUnitTest<?> test = grails.getUnitTest();
        for (Class<?> interceptorClass : interceptors) {
            Interceptor interceptor = (Interceptor) test.getApplicationContext().getBean(interceptorClass);
            if (!interceptor.doesMatch(test.getRequest())) {
                throw new AssertionError("Interceptor " + interceptorClass.getName() + " should match but didn't!");
            }
        }
    }

    private static void mockInterceptorAdapter(ControllerUnitTest<?> test) {
        BeanDefinitionRegistry registry = requireRegistry(test.getApplicationContext());
        if (registry.containsBeanDefinition(INTERCEPTOR_ADAPTER_BEAN)) {
            return;
        }
        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(GrailsInterceptorHandlerInterceptorAdapter.class);
        registry.registerBeanDefinition(INTERCEPTOR_ADAPTER_BEAN, def);
    }

    private void mockInterceptor(ControllerUnitTest<?> test, Class<?> interceptorClass) {
        GrailsClass artefact = test.getGrailsApplication().addArtefact(InterceptorArtefactHandler.TYPE, interceptorClass);
        BeanDefinitionRegistry registry = requireRegistry(test.getApplicationContext());
        if (!registry.containsBeanDefinition(artefact.getPropertyName())) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(artefact.getClazz());
            registry.registerBeanDefinition(artefact.getPropertyName(), def);
        }
        if (autowired.contains(interceptorClass)) {
            Object interceptor = test.getApplicationContext().getBean(artefact.getPropertyName());
            AutowireCapableBeanFactory factory = test.getApplicationContext().getAutowireCapableBeanFactory();
            factory.autowireBeanProperties(interceptor, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
        }
    }

    private GruContext afterCompletionWithError(ControllerUnitTest<?> test, GruContext context, Exception e) {
        try {
            interceptorAdapter.afterCompletion(test.getRequest(), test.getResponse(), this, e);
            return context;
        } catch (Throwable t) {
            return context.withError(t);
        }
    }

    private static BeanDefinitionRegistry requireRegistry(ConfigurableApplicationContext ctx) {
        if (!(ctx instanceof BeanDefinitionRegistry)) {
            throw new IllegalStateException("Application context is not a BeanDefinitionRegistry: " + ctx);
        }
        return (BeanDefinitionRegistry) ctx;
    }
}
