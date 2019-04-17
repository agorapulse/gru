package com.agorapulse.gru;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultMultipartDefinitionBuilder implements MultipartDefinitionBuilder, MultipartDefinition {

    private final Client client;
    private final Map<String, Object> parameters = new LinkedHashMap<>();
    private final Map<String, MultipartFile> files = new LinkedHashMap<>();

    public DefaultMultipartDefinitionBuilder(Client client) {
        this.client = client;
    }

    @Override
    public MultipartDefinitionBuilder params(Map<String, Object> params) {
        parameters.putAll(params);
        return this;
    }

    @Override
    public MultipartDefinitionBuilder file(String parameterName, String filename, Content content, String contentType) {
        files.put(parameterName, new DefaultMultipartFile(client, parameterName, filename, content, contentType));
        return this;
    }

    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public Map<String, MultipartFile> getFiles() {
        return Collections.unmodifiableMap(files);
    }

}
