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
import org.springframework.web.servlet.ModelAndView

/**
 * Minion responsible for executing interceptors.
 */
@CompileStatic
class InterceptorsMinion extends AbstractMinion<Grails> {

    final int index = INTERCEPTORS_MINION_INDEX

    List<Class> interceptors = []

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
            interceptorAdapter.setInterceptors(grails.unitTest.applicationContext.getBeansOfType(Interceptor).values() as Interceptor[])

            if (!interceptorAdapter.preHandle(grails.unitTest.request, grails.unitTest.response, null)) {
                return context.withError(new NotHandled())
            }
        }
        return context
    }

    @CompileDynamic
    private static mockInterceptorAdapter(Grails grails) {
        grails.unitTest.defineBeans {
            grailsInterceptorHandlerInterceptorAdapter(GrailsInterceptorHandlerInterceptorAdapter)
        }
    }

    @Override
    @SuppressWarnings(['Instanceof', 'CatchException'])
    GruContext doAfterRun(Grails grails, Squad squad, GruContext context) {
        if (!interceptorAdapter) {
            return context
        }

        boolean preHandled = !context.hasError(NotHandled)
        context.cleanError(NotHandled)
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
                    String actionName = squad.ask(UrlMappingsMinion) { getActionName(grails.unitTest) }
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

    @SuppressWarnings('CatchThrowable')
    private GruContext afterCompletionWithError(Grails grails, GruContext context, Exception e) {
        try {
            interceptorAdapter.afterCompletion(grails.unitTest.request, grails.unitTest.response, this, e)
            return context
        } catch (Throwable t) {
            return context.withError(t)
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
    private static void mockInterceptor(Grails grails, Class<?> interceptorClass) {
        GrailsClass artefact = grails.unitTest.grailsApplication.addArtefact(InterceptorArtefactHandler.TYPE, interceptorClass)
        grails.unitTest.defineBeans {
            "${artefact.propertyName}"(artefact.clazz)
        }
    }

}
