import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import org.junit.Rule
import spock.lang.Specification

class InDefaultPackageTest extends Specification {

    @Rule Gru<Http> gru = Gru.equip(Http.steal(this)).prepare {
        baseUri 'http://despicableme.wikia.com'
    }

    void 'despicable me'() {
        expect:
            gru.test {
                get "/wiki/Felonius_Gru"
            }
    }

    void 'get fixture location'() {
        given:
            Http client = Http.steal(this)
        expect:
            client.getFixtureLocation('file.json') == 'InDefaultPackageTest/file.json'
            client.unitTest == this

    }

}
