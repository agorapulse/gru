package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.Content
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.jsonunit.MatchesIsoDateNow
import com.agorapulse.gru.jsonunit.MatchesPattern
import com.agorapulse.gru.jsonunit.MatchesUrl
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert

import java.util.function.Function

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
class PayloadMinion extends AbstractMinion<Client> {

    final int index = CONTENT_MINION_INDEX

    Content payload
    String contentType

    PayloadMinion() {
        super(Client)
    }

    @Override
    GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (payload) {
            InputStream inputStream = payload?.load(client)
            if (!inputStream && payload.saveSupported) {
                payload.save(client, new ByteArrayInputStream(new byte[0]))
                log.warning("Content missing for $payload. Content was saved.")
                inputStream = new ByteArrayInputStream(new byte[0])
            }
            client.request.setContent(contentType, inputStream.bytes)
        }
        context
    }

}
