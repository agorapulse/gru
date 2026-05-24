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
package com.agorapulse.gru.grails.minions.jsonview;

import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.grails.Grails;
import com.agorapulse.gru.grails.minions.UrlMappingsMinion;
import com.agorapulse.gru.minions.AbstractMinion;
import com.agorapulse.gru.minions.JsonMinion;
import grails.core.GrailsApplication;
import grails.plugin.json.view.JsonViewConfiguration;
import grails.plugin.json.view.JsonViewTemplateEngine;
import grails.plugin.json.view.api.JsonView;
import grails.plugin.json.view.api.jsonapi.DefaultJsonApiIdRenderer;
import grails.plugin.json.view.mvc.JsonViewResolver;
import grails.plugin.json.view.test.JsonRenderResult;
import grails.testing.web.controllers.ControllerUnitTest;
import grails.views.WritableScriptTemplate;
import grails.views.api.HttpView;
import grails.views.mvc.GenericGroovyTemplateViewResolver;
import grails.views.resolve.PluginAwareTemplateResolver;
import groovy.text.Template;
import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext;
import org.grails.datastore.mapping.model.MappingContext;
import org.grails.web.mapping.DefaultLinkGenerator;
import org.grails.web.mapping.UrlMappingsHolderFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Optional minion that renders JSON views when the controller returned a model
 * (instead of writing to the response). Only registered when the JSON views
 * plugin is on the classpath.
 */
public class JsonViewRendererMinion extends AbstractMinion<Grails> {

    private static final String GRAILS_DOMAIN_CLASS_MAPPING_CONTEXT = "grailsDomainClassMappingContext";
    private static final String JSON_TEMPLATE_ENGINE_BEAN = "jsonTemplateEngine";
    private static final String GRAILS_LINK_GENERATOR_BEAN = "grailsLinkGenerator";
    private static final String LOCALE_RESOLVER_BEAN = "localeResolver";
    private static final String URL_MAPPINGS_HOLDER_BEAN = "grailsUrlMappingsHolder";
    private static final String JSON_API_ID_RENDER_STRATEGY_BEAN = "jsonApiIdRenderStrategy";
    private static final String JSON_VIEW_CONFIGURATION_BEAN = "jsonViewConfiguration";
    private static final String JSON_SMART_VIEW_RESOLVER_BEAN = "jsonSmartViewResolver";
    private static final String JSON_VIEW_RESOLVER_BEAN = "jsonViewResolver";

    private JsonViewTemplateEngine templateEngine;
    private MappingContext mappingContext;

    public JsonViewRendererMinion() {
        super(Grails.class);
    }

    @Override
    public int getIndex() {
        return HTTP_MINION_INDEX - 1000;
    }

    @Override
    protected GruContext doBeforeRun(Grails client, Squad squad, GruContext context) {
        setupJsonViewsPlugin(client);
        return context;
    }

    private void setupJsonViewsPlugin(Grails grails) {
        ControllerUnitTest<?> test = grails.getUnitTest();
        GrailsApplication ga = test.getGrailsApplication();
        ConfigurableApplicationContext ctx = test.getApplicationContext();
        if (!(ctx instanceof BeanDefinitionRegistry)) {
            throw new IllegalStateException("Application context is not a BeanDefinitionRegistry: " + ctx);
        }
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ctx;
        Set<String> existing = new HashSet<>(java.util.Arrays.asList(registry.getBeanDefinitionNames()));

        if (!existing.contains(GRAILS_LINK_GENERATOR_BEAN)) {
            String serverUrl = resolveServerUrl(ga);
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(DefaultLinkGenerator.class);
            def.getConstructorArgumentValues().addGenericArgumentValue(serverUrl);
            registry.registerBeanDefinition(GRAILS_LINK_GENERATOR_BEAN, def);
        }
        if (!existing.contains(LOCALE_RESOLVER_BEAN)) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(SessionLocaleResolver.class);
            registry.registerBeanDefinition(LOCALE_RESOLVER_BEAN, def);
        }
        if (!existing.contains(URL_MAPPINGS_HOLDER_BEAN)) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(UrlMappingsHolderFactoryBean.class);
            def.getPropertyValues().add("grailsApplication", ga);
            registry.registerBeanDefinition(URL_MAPPINGS_HOLDER_BEAN, def);
        }
        if (!existing.contains(GRAILS_DOMAIN_CLASS_MAPPING_CONTEXT)) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(KeyValueMappingContext.class);
            def.getConstructorArgumentValues().addGenericArgumentValue("test");
            def.getPropertyValues().add("canInitializeEntities", true);
            registry.registerBeanDefinition(GRAILS_DOMAIN_CLASS_MAPPING_CONTEXT, def);
        }
        if (!existing.contains(JSON_API_ID_RENDER_STRATEGY_BEAN)) {
            registerSimple(registry, JSON_API_ID_RENDER_STRATEGY_BEAN, DefaultJsonApiIdRenderer.class);
            registerSimple(registry, JSON_VIEW_CONFIGURATION_BEAN, JsonViewConfiguration.class);

            RootBeanDefinition engineDef = new RootBeanDefinition();
            engineDef.setBeanClass(JsonViewTemplateEngine.class);
            engineDef.getConstructorArgumentValues().addGenericArgumentValue(new org.springframework.beans.factory.config.RuntimeBeanReference(JSON_VIEW_CONFIGURATION_BEAN));
            engineDef.getConstructorArgumentValues().addGenericArgumentValue(JsonViewRendererMinion.class.getClassLoader());
            registry.registerBeanDefinition(JSON_TEMPLATE_ENGINE_BEAN, engineDef);

            RootBeanDefinition templateResolverDef = new RootBeanDefinition();
            templateResolverDef.setBeanClass(PluginAwareTemplateResolver.class);
            templateResolverDef.getConstructorArgumentValues().addGenericArgumentValue(new org.springframework.beans.factory.config.RuntimeBeanReference(JSON_VIEW_CONFIGURATION_BEAN));

            RootBeanDefinition smartResolverDef = new RootBeanDefinition();
            smartResolverDef.setBeanClass(JsonViewResolver.class);
            smartResolverDef.getConstructorArgumentValues().addGenericArgumentValue(new org.springframework.beans.factory.config.RuntimeBeanReference(JSON_TEMPLATE_ENGINE_BEAN));
            smartResolverDef.getPropertyValues().add("templateResolver", templateResolverDef);
            registry.registerBeanDefinition(JSON_SMART_VIEW_RESOLVER_BEAN, smartResolverDef);

            RootBeanDefinition viewResolverDef = new RootBeanDefinition();
            viewResolverDef.setBeanClass(GenericGroovyTemplateViewResolver.class);
            viewResolverDef.getConstructorArgumentValues().addGenericArgumentValue(new org.springframework.beans.factory.config.RuntimeBeanReference(JSON_SMART_VIEW_RESOLVER_BEAN));
            registry.registerBeanDefinition(JSON_VIEW_RESOLVER_BEAN, viewResolverDef);
        }

        if (templateEngine == null) {
            templateEngine = ctx.getBean(JSON_TEMPLATE_ENGINE_BEAN, JsonViewTemplateEngine.class);
        }
        if (mappingContext == null) {
            mappingContext = ctx.getBean(GRAILS_DOMAIN_CLASS_MAPPING_CONTEXT, MappingContext.class);
        }
    }

    private static String resolveServerUrl(GrailsApplication ga) {
        try {
            Object serverUrl = ga.getConfig().navigate("grails", "serverURL");
            if (serverUrl != null) {
                return serverUrl.toString();
            }
        } catch (Exception ignored) {
            // fall through to default
        }
        return "http://localhost:8080";
    }

    private static void registerSimple(BeanDefinitionRegistry registry, String name, Class<?> type) {
        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(type);
        registry.registerBeanDefinition(name, def);
    }

    public JsonRenderResult render(String viewUri, Map<String, Object> model) {
        WritableScriptTemplate template = templateEngine.resolveTemplate(viewUri);

        if (template == null) {
            throw new IllegalArgumentException("No view or template found for URI " + viewUri);
        }

        return produceResult(template, model);
    }

    @Override
    protected GruContext doAfterRun(Grails grails, Squad squad, GruContext context) {
        String actualResponseText = grails.getResponse().getText();
        if (actualResponseText != null && !actualResponseText.isEmpty()) {
            return context;
        }
        if (!(context.getResult() instanceof Map)) {
            return context;
        }
        Object responseContent = squad.ask(JsonMinion.class, JsonMinion::getResponseContent);
        if (responseContent == null) {
            return context;
        }

        ControllerUnitTest<?> test = grails.getUnitTest();
        String controllerName = squad.ask(UrlMappingsMinion.class, m -> m.getControllerName(test));
        String actionName = squad.ask(UrlMappingsMinion.class, m -> m.getActionName(test));

        JsonRenderResult result;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) context.getResult();
            result = render("/" + controllerName + "/" + actionName, resultMap);
        } catch (Exception e) {
            return context.withError(e);
        }

        if (result.getStatus() != HttpStatus.OK) {
            boolean committed = grails.getResponse().getResponse().isCommitted();
            grails.getResponse().getResponse().setCommitted(false);
            grails.getResponse().getResponse().setStatus(result.getStatus().value());
            grails.getResponse().getResponse().setCommitted(committed);
        }

        if (result.getHeaders() != null) {
            result.getHeaders().forEach((k, v) -> grails.getResponse().getResponse().addHeader(k, v));
        }

        if (result.getContentType() != null) {
            grails.getResponse().getResponse().setContentType(result.getContentType());
        }

        if (result.getJsonText() != null) {
            try {
                grails.getResponse().getResponse().getWriter().write(result.getJsonText());
            } catch (java.io.IOException e) {
                return context.withError(e);
            }
        }

        return context;
    }

    private static JsonRenderResult produceResult(Template template, Map<String, Object> model) {
        JsonView writable = (JsonView) template.make(model);

        JsonRenderResult result = new JsonRenderResult();
        if (writable instanceof HttpView) {
            HttpView httpView = (HttpView) writable;
            httpView.setResponse(new TestHttpResponse(result));
        }

        StringWriter sw = new StringWriter();
        try {
            writable.writeTo(sw);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Failed to render JSON view", e);
        }
        result.setJsonText(sw.toString());
        return result;
    }
}
