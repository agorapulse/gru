package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.TestClient
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
            Client client = new TestClient(this, null, response)
            Squad squad = new Squad()
        expect:
            client.unitTest == this
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

}
