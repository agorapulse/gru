package com.agorapulse.gru.minions

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.agorapulse.gru.minions.AbstractContentMinion.TEST_RESOURCES_FOLDER_PROPERTY_NAME

/**
 * Tests for TextMinion.
 */
class TextMinionSpec extends Specification {

    private static final String TEXT = 'Hello World'
    private static final String FILE_NAME = 'hello.txt'
    private static final String EXPECTED_FILE_LOCATION = 'com/agorapulse/gru/minions/TextMinionSpec/' + FILE_NAME

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    @SuppressWarnings('UnnecessaryGetter')
    void 'fixture file is created'() {
        given:
            File testResourcesFolder = tmp.newFolder()
            System.setProperty(TEST_RESOURCES_FOLDER_PROPERTY_NAME, testResourcesFolder.canonicalPath)
            TextMinion textMinion = new TextMinion(responseFile: FILE_NAME)
            Client.Response response = Mock(Client.Response) {
                getText() >> TEXT
            }
            Client client = new TestClient(this, response)
            Squad squad = new Squad()
        when:
            textMinion.verify(client, squad, GruContext.EMPTY)
        then:
            AssertionError error = thrown(AssertionError)
            error.message
            error.message.startsWith('New fixture files were created')
        when:
            File fixtureFile = new File(testResourcesFolder, EXPECTED_FILE_LOCATION)
        then:
            fixtureFile.exists()
            fixtureFile.text == TEXT

    }

    static class TestClient extends AbstractClient {
        final Client.Response response

        TestClient(Object unitTest, Client.Response response) {
            super(unitTest)
            this.response = response
        }

        @Override
        Client.Request getRequest() {
            throw new UnsupportedOperationException()
        }

        @Override
        void reset() { }

        @Override
        GruContext run(Squad squad, GruContext context) {
            throw new UnsupportedOperationException()
        }
    }

}
