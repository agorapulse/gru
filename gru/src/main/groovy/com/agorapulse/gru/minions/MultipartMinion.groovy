package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.DefaultMultipartDefinitionBuilder
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.MultipartDefinitionBuilder
import com.agorapulse.gru.Squad
import groovy.transform.CompileStatic
import groovy.util.logging.Log

import java.util.function.Consumer

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
class MultipartMinion extends AbstractMinion<Client> {

    final int index = MULTIPART_MINION_INDEX

    private Consumer<MultipartDefinitionBuilder> multipartDefinition

    MultipartMinion() {
        super(Client)
    }

    @Override
    GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (multipartDefinition) {
            DefaultMultipartDefinitionBuilder definition = new DefaultMultipartDefinitionBuilder(client)
            multipartDefinition.accept(definition)
            client.request.multipart = definition
        }
        return context
    }

    void multipart(Consumer<MultipartDefinitionBuilder> definition) {
        this.multipartDefinition = definition
    }
}
