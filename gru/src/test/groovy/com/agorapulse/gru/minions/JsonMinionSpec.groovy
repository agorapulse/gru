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
class JsonMinionSpec extends Specification {

    private static final String JSON = '{\n\n}'
    private static final String FILE_NAME = 'empty.json'
    private static final String EXPECTED_FILE_LOCATION = 'com/agorapulse/gru/minions/JsonMinionSpec/' + FILE_NAME

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    void 'request fixture file is created'() {
        given:
            File testResourcesFolder = tmp.newFolder()
            System.setProperty(TEST_RESOURCES_FOLDER_PROPERTY_NAME, testResourcesFolder.canonicalPath)
            JsonMinion jsonMinion = new JsonMinion(requestJsonFile: FILE_NAME)
            Client client = new TestClient(this, Mock(Client.Request), Mock(Client.Response))
            Squad squad = new Squad()
        when:
            jsonMinion.beforeRun(client, squad, GruContext.EMPTY)
            File fixtureFile = new File(testResourcesFolder, EXPECTED_FILE_LOCATION)
        then:
            fixtureFile.exists()
            fixtureFile.text == JSON

    }

}
