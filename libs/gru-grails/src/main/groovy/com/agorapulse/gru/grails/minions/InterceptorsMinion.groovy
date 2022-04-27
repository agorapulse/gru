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
import grails.artefact.Interceptor
import grails.core.GrailsClass
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.plugins.web.interceptors.GrailsInterceptorHandlerInterceptorAdapter
import org.grails.plugins.web.interceptors.InterceptorArtefactHandler
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.web.servlet.ModelAndView

/**
 * Minion responsible for executing interceptors.
 */
@CompileStatic
class InterceptorsMinion extends AbstractMinion<Grails> {

    final int index = INTERCEPTORS_MINION_INDEX

    List<Class> interceptors = []
    List<Class> autowired = []

    GrailsInterceptorHandlerInterceptorAdapter interceptorAdapter

    InterceptorsMinion() {
        super(Grails)
    }

    @Override
    GruContext doBeforeRun(Grails grails, Squad squad, GruContext context) {
        if (interceptors) {
            mockInterceptorAdapter(grails)

            interceptors.each {
                mockInterceptor(grails, it)
            }

            interceptorAdapter = grails.unitTest.applicationContext.getBean(GrailsInterceptorHandlerInterceptorAdapter)
            interceptorAdapter.setInterceptors(grails.unitTest.applicationContext.getBeansOfType(Interceptor, true, false).values() as Interceptor[])

            if (!interceptorAdapter.preHandle(grails.unitTest.request, grails.unitTest.response, null)) {
                return context.withError(new NotHandled())
            }
        }
        return context
    }

    @Override
    @SuppressWarnings(['Instanceof', 'CatchException'])
    GruContext doAfterRun(Grails grails, Squad squad, GruContext ctx) {
        GruContext context = ctx

        if (!interceptorAdapter) {
            return context
        }

        boolean preHandled = !context.hasError(NotHandled)
        context = context.cleanError(NotHandled)
        if (context.hasError(Exception)) {
            return afterCompletionWithError(grails, context, context.error as Exception)
        }
        try {
            if (preHandled) {
                ModelAndView modelAndView = null
                Object modelAndViewObject = grails.unitTest.request.getAttribute(GrailsApplicationAttributes.MODEL_AND_VIEW)
                if (modelAndViewObject instanceof ModelAndView) {
                    modelAndView = (ModelAndView) modelAndViewObject
                } else if (context.result instanceof Map) {
                    String actionName = squad.ask(UrlMappingsMinion) { UrlMappingsMinion url ->  url.getActionName(grails.unitTest) }
                    modelAndView =  new ModelAndView(actionName, new HashMap<String, Object>(context.result as Map))
                } else if (context.result instanceof ModelAndView) {
                    modelAndView = context.result as ModelAndView
                }
                interceptorAdapter.postHandle(grails.unitTest.request, grails.unitTest.response, this, modelAndView)
            }
            return context
        } catch (Exception e) {
            return afterCompletionWithError(grails, context, e)
        }
    }

    @Override
    void doVerify(Grails grails, Squad squad, GruContext resultAndError) throws Throwable {
        if (interceptors) {
            for (Class interceptorClass in interceptors) {
                Interceptor interceptor = grails.unitTest.applicationContext.getBean(interceptorClass) as Interceptor
                if (!interceptor.doesMatch(grails.unitTest.request)) {
                    throw new AssertionError("Interceptor $interceptorClass.name should match but didn't!")
                }
            }
        }
    }

    @CompileDynamic
    private static void mockInterceptorAdapter(Grails grails) {
        grails.unitTest.defineBeans {
            grailsInterceptorHandlerInterceptorAdapter(GrailsInterceptorHandlerInterceptorAdapter)
        }
    }

    @SuppressWarnings('CatchThrowable')
    private GruContext afterCompletionWithError(Grails grails, GruContext context, Exception e) {
        try {
            interceptorAdapter.afterCompletion(grails.unitTest.request, grails.unitTest.response, this, e)
            return context
        } catch (Throwable t) {
            return context.withError(t)
        }
    }

    @CompileDynamic
    private void mockInterceptor(Grails grails, Class<?> interceptorClass) {
        GrailsClass artefact = grails.unitTest.grailsApplication.addArtefact(InterceptorArtefactHandler.TYPE, interceptorClass)
        grails.unitTest.defineBeans {
            "${artefact.propertyName}"(artefact.clazz)
        }
        if (interceptorClass in autowired) {
            Object interceptor = grails.unitTest.applicationContext.getBean("${artefact.propertyName}")
            AutowireCapableBeanFactory factory = grails.unitTest.applicationContext.autowireCapableBeanFactory
            factory.autowireBeanProperties(interceptor, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false)
        }
    }

}
