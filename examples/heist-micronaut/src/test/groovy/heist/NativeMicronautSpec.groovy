package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.micronaut.Micronaut
import io.micronaut.context.ApplicationContext
import io.micronaut.context.ApplicationContextProvider
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
class NativeMicronautSpec extends Specification implements ApplicationContextProvider {

    @AutoCleanup Gru gru = Gru.create(Micronaut.create(this))

    @Inject ApplicationContext applicationContext

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
