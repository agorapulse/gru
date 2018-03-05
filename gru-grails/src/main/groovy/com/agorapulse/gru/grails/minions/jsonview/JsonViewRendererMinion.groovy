package com.agorapulse.gru.grails.minions.jsonview

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.grails.minions.UrlMappingsMinion
import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.minions.JsonMinion
import grails.core.GrailsApplication
import grails.plugin.json.view.JsonViewGrailsPlugin
import grails.plugin.json.view.JsonViewTemplateEngine
import grails.plugin.json.view.api.JsonView
import grails.plugin.json.view.test.JsonRenderResult
import grails.views.api.HttpView
import groovy.text.Template
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.datastore.mapping.model.MappingContext
import org.grails.web.mapping.DefaultLinkGenerator
import org.grails.web.mapping.UrlMappingsHolderFactoryBean
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.i18n.SessionLocaleResolver

/**
 * Minion responsible for HTML responses which is able to generate HTML from view.
 */
@Log
@CompileStatic
class JsonViewRendererMinion extends AbstractMinion<Grails> {

    final int index = HTTP_MINION_INDEX - 1000

    private JsonViewTemplateEngine templateEngine
    private MappingContext mappingContext

    JsonViewRendererMinion() {
        super(Grails)
    }

    GruContext doBeforeRun(Grails client, Squad squad, GruContext context) {
        setupJsonViewsPlugin(client)
        return context
    }

    @CompileDynamic
    void setupJsonViewsPlugin(Grails grails) {
        if (templateEngine && mappingContext) {
            return
        }

        GrailsApplication grailsApplication = grails.unitTest.grailsApplication
        def config = grailsApplication.config

        grails.unitTest.defineBeans {
            grailsLinkGenerator(DefaultLinkGenerator, config?.grails?.serverURL ?: "http://localhost:8080")
            localeResolver(SessionLocaleResolver)
            grailsUrlMappingsHolder(UrlMappingsHolderFactoryBean) {
                grailsApplication = grailsApplication
            }
            grailsDomainClassMappingContext(KeyValueMappingContext, 'test') {
                canInitializeEntities = true
            }
        }

        grails.unitTest.defineBeans(new JsonViewGrailsPlugin())


        if (templateEngine == null) {
            templateEngine = grails.unitTest.applicationContext.getBean('jsonTemplateEngine', JsonViewTemplateEngine)
        }
        if (mappingContext == null) {
            mappingContext = grails.unitTest.applicationContext.getBean('grailsDomainClassMappingContext', MappingContext)
        }
    }

    @Override
    protected GruContext doAfterRun(Grails grails, Squad squad, GruContext context) {
        String actualResponseText = grails.response.text

        if (!actualResponseText && context.result instanceof Map && squad.ask(JsonMinion) { responseFile }) {
            String controllerName = squad.ask(UrlMappingsMinion) {
                getControllerName(grails.unitTest)
            }

            String actionName = squad.ask(UrlMappingsMinion) {
                getActionName(grails.unitTest)
            }

            JsonRenderResult result = render("/$controllerName/$actionName", context.result as Map)

            if (result.status != HttpStatus.OK) {
                boolean committed = grails.response.response.committed
                grails.response.response.committed = false
                grails.response.response.status = result.status.value()
                grails.response.response.committed = committed
            }

            if (result.headers) {
                result.headers.each {
                    grails.response.response.addHeader(it.key, it.value)
                }
            }

            if (result.contentType) {
                grails.response.response.contentType = result.contentType
            }

            if (result.jsonText) {
                grails.response.response.writer.write(result.jsonText)
            }

            return context
        }

        return context
    }

    JsonRenderResult render(String viewUri, Map model) {
        def template = templateEngine.resolveTemplate(viewUri)

        if (template == null) {
            throw new IllegalArgumentException("No view or template found for URI $viewUri")
        }

        return produceResult(template, model)
    }

    private static JsonRenderResult produceResult(Template template, Map model) {
        JsonView writable = (JsonView) template.make(model)

        def result = new JsonRenderResult()
        if (writable instanceof HttpView) {
            def httpView = (HttpView) writable
            httpView.setResponse(new TestHttpResponse(result))
        }

        def sw = new StringWriter()
        writable.writeTo(sw)
        def str = sw.toString()
        result.jsonText = str
        return result
    }

}
