package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.micronaut.Micronaut
import spock.lang.AutoCleanup
import spock.lang.Specification

class AutomaticMicronautSpec extends Specification {

    @AutoCleanup Gru gru = Gru.create(Micronaut.create(this))

    void 'test it works'() {
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moon.json'
                }
            }
    }

}
