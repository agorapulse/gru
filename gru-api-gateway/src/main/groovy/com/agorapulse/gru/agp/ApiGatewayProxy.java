package com.agorapulse.gru.agp;

import com.agorapulse.gru.AbstractClient;
import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.google.common.base.Preconditions;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ApiGatewayProxy extends AbstractClient {

    public static ApiGatewayProxy steal(Object unitTest,
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ApiGatewayConfiguration.class)
                                        @ClosureParams(value = FromString.class, options = "com.agorapulse.gru.agp.ApiGatewayConfiguration")
                                        Closure<ApiGatewayConfiguration.Mapping> configuration
    ) {
        ApiGatewayConfiguration apiGatewayConfiguration = new ApiGatewayConfiguration();
        DefaultGroovyMethods.with(apiGatewayConfiguration, configuration);
        return new ApiGatewayProxy(unitTest, apiGatewayConfiguration);
    }

    private final ApiGatewayConfiguration configuration;

    private ApiGatewayProxy(Object unitTest, ApiGatewayConfiguration apiGatewayConfiguration) {
        super(unitTest);
        this.configuration = apiGatewayConfiguration;
    }

    private ApiGatewayProxyRequest request = new ApiGatewayProxyRequest();
    private ApiGatewayProxyResponse response;

    @Override
    public Client.Request getRequest() {
        return request;
    }

    @Override
    public Client.Response getResponse() {
        return Preconditions.checkNotNull(response, "Response hasn't been set yet");
    }

    @Override
    public void reset() {
        request = new ApiGatewayProxyRequest();
        response = null;
    }


    @Override
    public GruContext run(Squad squad, GruContext context) {
        try {
            this.response = new ApiGatewayProxyResponse(configuration
                .findMapping(request.getMethod(), request.getPath())
                .executeHandler(request, unitTest)
            );
            return context.withResult(this.response);
        } catch (ClassNotFoundException | IOException | IllegalAccessException | InvocationTargetException e) {
            return context.withError(e);
        }
    }
}
