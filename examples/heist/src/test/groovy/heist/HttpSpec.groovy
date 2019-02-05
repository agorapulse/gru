package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import org.junit.Rule
import spock.lang.Specification

class HttpSpec extends Specification{

    @Rule Gru<Http> gru = Gru.equip(Http.steal(this))                                   // <1>
                             .prepare('http://despicableme.fandom.com')                 // <2>

    void 'despicable me'() {
        expect:
            gru.test {
                get "/wiki/Felonius_Gru"                                                // <3>
            }
    }
}
