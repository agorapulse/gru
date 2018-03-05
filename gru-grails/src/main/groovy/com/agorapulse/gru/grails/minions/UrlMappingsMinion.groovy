package com.agorapulse.gru.grails.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.DefaultTestDefinitionBuilder
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.minions.AbstractMinion
import grails.core.GrailsControllerClass
import grails.testing.web.controllers.ControllerUnitTest
import grails.util.GrailsNameUtils
import grails.web.mapping.UrlMapping
import grails.web.mapping.UrlMappingInfo
import grails.web.mapping.UrlMappingsHolder
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import javassist.util.proxy.ProxyObject
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.core.artefact.ControllerArtefactHandler
import org.grails.core.artefact.UrlMappingsArtefactHandler
import org.grails.web.mapping.UrlMappingsHolderFactoryBean
import org.grails.web.mapping.mvc.GrailsControllerUrlMappings

/**
 * Minion responsible for url mappings.
 */
@CompileStatic
class UrlMappingsMinion extends AbstractMinion<Grails> {

    final int index = URL_MAPPINGS_MINION_INDEX

    private UrlMappingInfo urlMappingInfo = null
    private GrailsControllerUrlMappings urlMappingsHolder = null

    List<Class> urlMappings = []

    MethodClosure action

    UrlMappingsMinion() {
        super(Grails)
    }

    @Override
    GruContext doBeforeRun(Grails grails, Squad squad, GruContext context) {
        if (urlMappings.isEmpty() && grails.request.uri != null) {
            try {
                urlMappings = [getClass().classLoader.loadClass(DefaultTestDefinitionBuilder.UrlMappings)] as List<Class>
            } catch (ClassNotFoundException ignored) {
                return context.withError(new AssertionError(
                    (Object) 'URI for action is specified but UrlMappings is not defined nor default UrlMappings class exists!'
                ))
            }
        }

        try {
            readMappingInfo(grails.unitTest)
            grails.unitTest.params.putAll(urlMappingInfo.parameters)

            return context
        } catch (AssertionError ae) {
            return context.withError(ae)
        }
    }

    private void initUrlMappingsArtifacts(ControllerUnitTest unitTest) {
        for (Class urlMappingClass in urlMappings) {
            unitTest.grailsApplication.addArtefact(UrlMappingsArtefactHandler.TYPE, urlMappingClass)
        }
    }

    GrailsControllerUrlMappings getUrlMappingsHolder(ControllerUnitTest unitTest) {
        initUrlMappingsArtifacts(unitTest)
        defineMappingsHolder(unitTest)
        urlMappingsHolder = unitTest.applicationContext.getBean('grailsUrlMappingsHolder', GrailsControllerUrlMappings)
    }

    @CompileDynamic
    private void defineMappingsHolder(ControllerUnitTest unitTest) {
        unitTest.defineBeans {
            grailsUrlMappingsHolder(UrlMappingsHolderFactoryBean) {
                delegate.grailsApplication = unitTest.grailsApplication
            }
        }
    }

    private UrlMappingInfo readMappingInfo(ControllerUnitTest unitTest) {
        if (urlMappingInfo) {
            return urlMappingInfo
        }

        UrlMappingsHolder mappingsHolder = getUrlMappingsHolder(unitTest)
        List<UrlMappingInfo> mappingInfos = mappingsHolder.matchAll(unitTest.request.requestURI, unitTest.request.method ?: UrlMapping.ANY_HTTP_METHOD).toList()

        if (mappingInfos.size() == 0) {
            throw new AssertionError("URL '${unitTest.request.requestURI}' is not mapped with method ${unitTest.request.method}!")
        }

        if (!action) {
            return urlMappingInfo = mappingInfos.first()
        }

        GrailsControllerClass controllerClass = getControllerClass(unitTest)

        UrlMappingInfo mappingMatched = mappingInfos.find { mapping ->
            mapping.configure(unitTest.webRequest)
            return getControllerName(unitTest) == mapping.controllerName &&
                getActionName(unitTest) == (mapping.actionName ?: controllerClass.defaultAction)
        }

        if (!mappingMatched) {
            throw new AssertionError("${unitTest.request.method}: '$unitTest.request.requestURI' is not mapped to ${action.owner.class.name}.${action.method}!")
        }

        return urlMappingInfo = mappingMatched
    }

    final GrailsControllerClass getControllerClass(ControllerUnitTest unitTest) {
        return (GrailsControllerClass) unitTest.grailsApplication.getArtefactByLogicalPropertyName(ControllerArtefactHandler.TYPE, getControllerName(unitTest))
    }

    /**
     * @return name of the action
     */
    final String getActionName(ControllerUnitTest unitTest) {
        if (action) {
            return action.method
        }
        UrlMappingInfo info = readMappingInfo(unitTest)
        info.actionName ?: getControllerClass(unitTest).defaultAction
    }

    /**
     * @return name of the controller
     */
    @SuppressWarnings('Instanceof')
    final String getControllerName(ControllerUnitTest unitTest) {
        if (action) {
            Class controllerType = action.owner.class
            if (action.owner instanceof ProxyObject) {
                controllerType = action.owner.class.superclass
            }
            return GrailsNameUtils.getPropertyName(GrailsNameUtils.getLogicalName(controllerType, 'Controller'))
        }
        UrlMappingInfo info = readMappingInfo(unitTest)
        info.controllerName
    }
}
