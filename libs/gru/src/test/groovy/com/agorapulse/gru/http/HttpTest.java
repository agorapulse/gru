package com.agorapulse.gru.http;

import com.agorapulse.gru.Gru;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HttpTest {

    @Test
    void testCreateWithoutSpecifyingUnitTest() {
        try (Gru gru = Gru.create("http://localhost:8080")) {
            Assertions.assertNotNull(gru);
        }
    }

}
