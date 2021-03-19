package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.micronaut.Micronaut
import io.micronaut.context.ApplicationContext
import io.micronaut.context.ApplicationContextProvider
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Specification

class ManualMicronautSpec extends Specification implements ApplicationContextProvider {

    @AutoCleanup Gru gru = Gru.create(Micronaut.create(this))

    @AutoCleanup ApplicationContext applicationContext
    @AutoCleanup EmbeddedServer embeddedServer

    void setup() {
        applicationContext = ApplicationContext.builder().build()
        applicationContext.start()

        embeddedServer = applicationContext.getBean(EmbeddedServer)
        embeddedServer.start()
    }

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
