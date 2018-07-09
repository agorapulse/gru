package com.agorapulse.gru.agp;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

class MockLogger implements LambdaLogger {
    @Override
    public void log(String message) {
        System.err.println(message); // NOSONAR
    }

    @Override
    public void log(byte[] message) {
        log(new String(message));
    }

}
