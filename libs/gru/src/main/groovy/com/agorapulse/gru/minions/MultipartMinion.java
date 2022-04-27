package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.DefaultMultipartDefinitionBuilder;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.MultipartDefinitionBuilder;
import com.agorapulse.gru.Squad;

import java.util.function.Consumer;

/**
 * Minion responsible for JSON requests and responses.
 */
public class MultipartMinion extends AbstractMinion<Client> {
    private Consumer<MultipartDefinitionBuilder> multipartDefinition;

    public MultipartMinion() {
        super(Client.class);
    }

    @Override
    public GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (multipartDefinition != null) {
            DefaultMultipartDefinitionBuilder definition = new DefaultMultipartDefinitionBuilder(client);
            multipartDefinition.accept(definition);
            client.getRequest().setMultipart(definition);
        }

        return context;
    }

    public void multipart(Consumer<MultipartDefinitionBuilder> definition) {
        this.multipartDefinition = definition;
    }

    public final int getIndex() {
        return MULTIPART_MINION_INDEX;
    }

}
