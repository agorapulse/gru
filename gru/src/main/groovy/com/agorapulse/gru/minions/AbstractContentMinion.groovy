package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.Content
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import groovy.transform.CompileStatic
import groovy.util.logging.Log

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
abstract class AbstractContentMinion<C extends Client> extends AbstractMinion<C> {

    public static final String TEST_RESOURCES_FOLDER_PROPERTY_NAME = 'TEST_RESOURCES_FOLDER'

    Content responseContent

    protected final List<String> createdResources = []

    protected AbstractContentMinion(Class<C> clientType) {
        super(clientType)
    }

    @Override
    final void doVerify(Client client, Squad squad, GruContext context) throws Throwable {
        if (responseContent) {
            String expectedResponseText = responseContent?.load(client)?.text
            String responseText = readResponseText(client, squad, context)

            if (!expectedResponseText && responseText) {
                expectedResponseText = responseText
                if (responseContent.saveSupported) {
                    responseContent.save(client, new ByteArrayInputStream(normalize(expectedResponseText).bytes))
                    log.warning("Content is missing for $responseContent. New file was created with content:\n$expectedResponseText")
                    createdResources << responseContent.toString()
                }
            }

            if (expectedResponseText) {
                similar(normalize(responseText), normalize(expectedResponseText))
            }
        }

        if (createdResources) {
            throw new AssertionError("New fixture files were created: ${createdResources.join(', ')}. Please, run the test again to verify it is repeatable.")
        }
    }

    protected abstract void similar(String actual, String expected) throws AssertionError

    protected String normalize(String input) { input }

    @SuppressWarnings('UnusedMethodParameter')
    protected String readResponseText(Client client, Squad squad, GruContext context) { client.response.text }

    protected static String load(Client client, String fileName) {
        InputStream stream = client.loadFixture(fileName)
        if (stream == null) {
            return null
        }
        stream.text
    }

    protected void saveResource(String path, String content) {
        if (!content) {
            return
        }
        createdResources << path
        File file = new File(System.getProperty(TEST_RESOURCES_FOLDER_PROPERTY_NAME) ?: 'src/test/resources', path)
        file.parentFile.mkdirs()
        file.text = content
    }
}
