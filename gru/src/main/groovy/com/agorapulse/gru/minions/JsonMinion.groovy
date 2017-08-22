package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.jsonunit.MatchesIsoDateNow
import com.agorapulse.gru.jsonunit.MatchesPattern
import com.agorapulse.gru.jsonunit.MatchesUrl
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
class JsonMinion extends AbstractMinion<Client> {

    final int index = JSON_MINION_INDEX

    String requestJsonFile
    String responseJsonFile

    @SuppressWarnings('unchecked')
    Closure<JsonFluentAssert> jsonUnitConfiguration = (Closure<JsonFluentAssert>) Closure.IDENTITY.clone()

    private final List<String> createdResources = []

    JsonMinion() {
        super(Client)
    }

    @Override
    GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (requestJsonFile) {
            String requestText = load(client, requestJsonFile)
            if (!requestText) {
                saveRresource(client.getFixtureLocation(requestJsonFile), '{\n\n}')
                log.warning("JSON fixture file is missing at ${client.getFixtureLocation(requestJsonFile)}. New file was created.")
                requestText = '{}'
            }
            client.request.json = requestText
        }
        context
    }

    @Override
    void doVerify(Client client, Squad squad, GruContext resultAndError) throws AssertionError {
        if (responseJsonFile) {
            String responseText = load(client, responseJsonFile)
            if (!responseText) {
                responseText = client.response.text
                saveRresource(client.getFixtureLocation(responseJsonFile), JsonOutput.prettyPrint(responseText))
                log.warning("JSON fixture file is missing at ${client.getFixtureLocation(responseJsonFile)}. New file was created with content:\n$responseText")
            }
            assert !responseText || similar(client.response.text, responseText)
        }

        if (createdResources) {
            throw new AssertionError("New fixture files were created: ${createdResources.join(', ')}. Please, run the test again to verify it is repeatable.")
        }
    }

    private boolean similar(String actual, String expected) {
        JsonFluentAssert fluentAssert = assertThatJson(actual)
                .as("Response must match ${responseJsonFile} content")
                .withMatcher('isoDate', MatchesPattern.ISO_DATE)
                .withMatcher('isoDateNow', MatchesIsoDateNow.INSTANCE)
                .withMatcher('positiveIntegerString', MatchesPattern.POSITIVE_NUMBER_STRING)
                .withMatcher('url', MatchesUrl.INSTANCE)

        fluentAssert = fluentAssert.with jsonUnitConfiguration

        try {
            return fluentAssert.isEqualTo(expected)
        } catch (AssertionError error) {
            log.info 'ACTUAL:'
            log.info JsonOutput.prettyPrint(actual)
            log.info 'EXPECTED:'
            log.info JsonOutput.prettyPrint(expected)
            throw error
        }
    }

    private static String load(Client client, String fileName) {
        InputStream stream = client.loadFixture(fileName)
        if (stream == null) {
            return null
        }
        stream.text
    }

    private void saveRresource(String path, String content) {
        if (!content) {
            return
        }
        createdResources << path
        File file = new File(System.getProperty('TEST_RESOURCES_FOLDER') ?: 'src/test/resources', path)
        if (file.exists()) {
            throw new IllegalArgumentException("File $path already exists!")
        }
        file.parentFile.mkdirs()
        file.text = content
    }
}
