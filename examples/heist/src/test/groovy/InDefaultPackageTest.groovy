import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import org.junit.Rule
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class InDefaultPackageTest extends Specification {

    @Rule Gru<Http> gru = Gru.equip(Http.steal(this) {
        readTimeout(10, TimeUnit.SECONDS)
    }).prepare('https://despicableme.fandom.com')

    void 'despicable me'() {
        expect:
            gru.test {
                get "/wiki/Felonius_Gru"
            }
    }

}
