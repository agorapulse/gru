package com.agorapulse.gru

/**
 * Very simple testing client.
 */
class TestClient extends AbstractClient {

    final Client.Request request
    final Client.Response response

    TestClient(Object unitTest, Client.Request request, Client.Response response) {
        super(unitTest)
        this.request = request
        this.response = response
    }

    @Override
    void reset() { }

    @Override
    GruContext run(Squad squad, GruContext context) {
        context
    }
}
