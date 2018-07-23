package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.Content
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.jsonunit.MatchesIsoDateNow
import com.agorapulse.gru.jsonunit.MatchesPattern
import com.agorapulse.gru.jsonunit.MatchesUrl
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
class JsonMinion extends AbstractContentMinion<Client> {

    final int index = JSON_MINION_INDEX

    Content requestJsonContent

    @SuppressWarnings('unchecked')
    Closure<JsonFluentAssert> jsonUnitConfiguration = (Closure<JsonFluentAssert>) Closure.IDENTITY.clone()

    JsonMinion() {
        super(Client)
    }

    @Override
    GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (requestJsonContent) {
            String requestText = requestJsonContent?.load(this, client)?.text
            if (!requestText && requestJsonContent.saveSupported) {
                requestJsonContent.save(client, new ByteArrayInputStream('{\n\n}'.bytes))
                log.warning("Content missing for $requestJsonContent. Content was saved.")
                requestText = '{}'
            }
            client.request.json = requestText
        }
        context
    }

    protected void similar(String actual, String expected) throws AssertionError {
        JsonFluentAssert fluentAssert = assertThatJson(actual)
                .as("Response must match ${responseContent} content")
                .withMatcher('isoDate', MatchesPattern.ISO_DATE)
                .withMatcher('isoDateNow', MatchesIsoDateNow.INSTANCE)
                .withMatcher('positiveIntegerString', MatchesPattern.POSITIVE_NUMBER_STRING)
                .withMatcher('url', MatchesUrl.INSTANCE)

        fluentAssert = fluentAssert.with jsonUnitConfiguration

        try {
            fluentAssert.isEqualTo(expected)
        } catch (AssertionError error) {
            log.info 'ACTUAL:'
            log.info actual
            log.info 'EXPECTED:'
            log.info expected
            throw error
        }
    }
}
