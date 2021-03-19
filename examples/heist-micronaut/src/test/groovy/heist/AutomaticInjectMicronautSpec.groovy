package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.micronaut.Micronaut
import io.micronaut.context.env.Environment
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.inject.Inject

class AutomaticInjectMicronautSpec extends Specification {

    @AutoCleanup Gru gru = Gru.create(Micronaut.build(this).start(true))

    @Inject Environment environment

    void 'test it works'() {
        expect:
            environment
        and:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moon.json'
                }
            }
    }

}
