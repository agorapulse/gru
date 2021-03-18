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
package com.agorapulse.gru.grails.minions.jsonview

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.grails.minions.UrlMappingsMinion
import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.minions.JsonMinion
import grails.core.GrailsApplication
import grails.plugin.json.view.JsonViewConfiguration
import grails.plugin.json.view.JsonViewGrailsPlugin
import grails.plugin.json.view.JsonViewTemplateEngine
import grails.plugin.json.view.api.JsonView
import grails.plugin.json.view.api.jsonapi.DefaultJsonApiIdRenderer
import grails.plugin.json.view.mvc.JsonViewResolver
import grails.plugin.json.view.test.JsonRenderResult
import grails.views.WritableScriptTemplate
import grails.views.api.HttpView
import grails.views.mvc.GenericGroovyTemplateViewResolver
import grails.views.resolve.PluginAwareTemplateResolver
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

    private static final String GRAILS_DOMAIN_CLASS_MAPPING_CONTEXT = 'grailsDomainClassMappingContext'

    final int index = HTTP_MINION_INDEX - 1000

    private JsonViewTemplateEngine templateEngine
    private MappingContext mappingContext

    JsonViewRendererMinion() {
        super(Grails)
    }

    @Override
    GruContext doBeforeRun(Grails client, Squad squad, GruContext context) {
        setupJsonViewsPlugin(client)
        return context
    }

    @CompileDynamic
    void setupJsonViewsPlugin(Grails grails) {
        GrailsApplication ga = grails.unitTest.grailsApplication
        Object config = ga.config

        String[] names = grails.unitTest.applicationContext.beanDefinitionNames

        grails.unitTest.defineBeans {
            if (!names.contains('grailsLinkGenerator')) {
                grailsLinkGenerator(DefaultLinkGenerator, config?.grails?.serverURL ?: 'http://localhost:8080')
            }

            if (!names.contains('localeResolver')) {
                localeResolver(SessionLocaleResolver)
            }

            if (!names.contains('grailsUrlMappingsHolder')) {
                grailsUrlMappingsHolder(UrlMappingsHolderFactoryBean) {
                    grailsApplication = ga
                }
            }

            if (!names.contains(GRAILS_DOMAIN_CLASS_MAPPING_CONTEXT)) {
                grailsDomainClassMappingContext(KeyValueMappingContext, 'test') {
                    canInitializeEntities = true
                }
            }

            if (!names.contains('jsonApiIdRenderStrategy')) {
                jsonApiIdRenderStrategy(DefaultJsonApiIdRenderer)
                jsonViewConfiguration(JsonViewConfiguration)
                jsonTemplateEngine(JsonViewTemplateEngine, jsonViewConfiguration, JsonViewRendererMinion.classLoader)
                jsonSmartViewResolver(JsonViewResolver, jsonTemplateEngine) {
                    templateResolver = bean(PluginAwareTemplateResolver, jsonViewConfiguration)
                }
                jsonViewResolver(GenericGroovyTemplateViewResolver, jsonSmartViewResolver )
            }
        }

        if (templateEngine == null) {
            templateEngine = grails.unitTest.applicationContext.getBean('jsonTemplateEngine', JsonViewTemplateEngine)
        }
        if (mappingContext == null) {
            mappingContext = grails.unitTest.applicationContext.getBean(GRAILS_DOMAIN_CLASS_MAPPING_CONTEXT, MappingContext)
        }
    }

    JsonRenderResult render(String viewUri, Map model) {
        WritableScriptTemplate template = templateEngine.resolveTemplate(viewUri)

        if (template == null) {
            throw new IllegalArgumentException("No view or template found for URI $viewUri")
        }

        return produceResult(template, model)
    }

    @Override
    @SuppressWarnings(['Instanceof', 'CatchException'])
    protected GruContext doAfterRun(Grails grails, Squad squad, GruContext context) {
        String actualResponseText = grails.response.text

        if (!actualResponseText && context.result instanceof Map && squad.ask(JsonMinion) { responseContent }) {
            String controllerName = squad.ask(UrlMappingsMinion) {
                getControllerName(grails.unitTest)
            }

            String actionName = squad.ask(UrlMappingsMinion) {
                getActionName(grails.unitTest)
            }

            JsonRenderResult result
            try {
                result = render("/$controllerName/$actionName", context.result as Map)
            } catch (Exception e) {
                return context.withError(e)
            }

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

    @SuppressWarnings('Instanceof')
    private static JsonRenderResult produceResult(Template template, Map model) {
        JsonView writable = (JsonView) template.make(model)

        JsonRenderResult result = new JsonRenderResult()
        if (writable instanceof HttpView) {
            HttpView httpView = (HttpView) writable
            httpView.response = new TestHttpResponse(result)
        }

        StringWriter sw = new StringWriter()
        writable.writeTo(sw)
        String str = sw
        result.jsonText = str
        return result
    }

}
