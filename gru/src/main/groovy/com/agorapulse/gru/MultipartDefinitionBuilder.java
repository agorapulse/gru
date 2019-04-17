package com.agorapulse.gru;

import com.agorapulse.gru.content.FileContent;

import java.util.Collections;
import java.util.Map;

public interface MultipartDefinitionBuilder extends WithContentSupport {

    MultipartDefinitionBuilder params(Map<String, Object> params);

    default MultipartDefinitionBuilder param(String name, Object value) {
        return params(Collections.singletonMap(name, value));
    }

    MultipartDefinitionBuilder file(String parameterName, String filename, Content content, String contentType);

    default MultipartDefinitionBuilder file(String parameterName, String filename, String relativePath, String contentType) {
        return file(parameterName, filename, FileContent.create(relativePath), contentType);
    }

}
