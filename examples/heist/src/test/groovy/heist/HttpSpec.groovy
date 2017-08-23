package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import org.junit.Rule
import spock.lang.Specification

class HttpSpec extends Specification{

    @Rule Gru<Http> gru = Gru.equip(Http.steal(this)).prepare {                         // <1>
        baseUri 'http://despicableme.wikia.com'                                         // <2>
    }

    void 'despicable me'() {
        expect:
            gru.test {
                get "/wiki/Felonius_Gru"                                                // <3>
            }
    }
}
