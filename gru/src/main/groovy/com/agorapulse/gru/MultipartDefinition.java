package com.agorapulse.gru;

import java.util.Map;

public interface MultipartDefinition {

    interface MultipartFile {

        String getParameterName();
        String getFilename();
        byte[] getBytes();
        String getContentType();

    }

    Map<String, Object> getParameters();
    Map<String, MultipartFile> getFiles();

}
