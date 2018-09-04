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
    public static final String ENVIRONMENT_VARIABLE_REWRITE_RESOURCES = 'COM_AGORAPULSE_GRU_REWRITE'
    public static final String SYSTEM_PROPERTY_REWRITE_RESOURCES = 'com.agorapulse.gru.rewrite'

    Content responseContent
    boolean rewrite

    protected final List<String> createdResources = []

    protected AbstractContentMinion(Class<C> clientType) {
        super(clientType)
        rewrite = System.getenv(ENVIRONMENT_VARIABLE_REWRITE_RESOURCES) == true.toString() || System.getProperty(SYSTEM_PROPERTY_REWRITE_RESOURCES) == true.toString()
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
                try {
                    similar(normalize(responseText), normalize(expectedResponseText))
                } catch (AssertionError assertionError) {
                    if (rewrite && rewriteSupported && responseContent.saveSupported) {
                        rewrite(client, normalize(responseText), normalize(expectedResponseText))
                    } else {
                        throw assertionError
                    }
                }
            }
        }

        if (createdResources) {
            throw new AssertionError("Fixture files were created or updated: ${createdResources.join(', ')}. Please, run the test again to verify it is repeatable.")
        }
    }

    protected abstract void similar(String actual, String expected) throws AssertionError

    protected String normalize(String input) { input }

    protected boolean isRewriteSupported() { false }
    protected String rewrite(Client client, String actualText, String expectedText) {
        throw new UnsupportedOperationException("Rewrite is not supported")
    }

    @SuppressWarnings('UnusedMethodParameter')
    protected String readResponseText(Client client, Squad squad, GruContext context) { client.response.text }
}
