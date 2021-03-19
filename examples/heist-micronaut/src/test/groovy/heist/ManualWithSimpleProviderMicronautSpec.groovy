package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.micronaut.Micronaut
import io.micronaut.context.ApplicationContext
import io.micronaut.context.ApplicationContextProvider
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Specification

class ManualWithSimpleProviderMicronautSpec extends Specification {

    @AutoCleanup Gru gru = Gru.create(Micronaut.create(this) { context })

    @AutoCleanup ApplicationContext context
    @AutoCleanup EmbeddedServer embeddedServer

    void setup() {
        context = ApplicationContext.builder().build()
        context.start()

        embeddedServer = context.getBean(EmbeddedServer)
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
