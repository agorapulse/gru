package com.agorapulse.gru;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//CHECKSTYLE:OFF
public interface HttpVerbsShortcuts {
    String HEAD = "HEAD";
    String POST = "POST";
    String PUT = "PUT";
    String PATCH = "PATCH";
    String DELETE = "DELETE";
    String OPTIONS = "OPTIONS";
    String TRACE = "TRACE";
    String GET = "GET";

    List<String> HAS_URI_PARAMETERS = Collections.unmodifiableList(Arrays.asList(
        HEAD, DELETE, OPTIONS, TRACE, GET
    ));


}
