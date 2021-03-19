package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.micronaut.Micronaut
import io.micronaut.context.env.Environment
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.inject.Inject

class AdvancedAutomaticMicronautSpec extends Specification {

    MoonService moonService = Mock()

    @AutoCleanup Gru gru = Gru.create(
        Micronaut.build(this) {
            environments 'my-custom-env'
        }.then {
            registerSingleton(MoonService, moonService)
        }.start(true)
    )

    @Inject Environment environment

    void 'test it works'() {
        expect:
            'my-custom-env' in environment.activeNames
        when:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moon.json'
                }
            }
        then:
            gru.verify()

            1 * moonService.get('earth', 'moon') >> new Moon('earth', 'moon')
    }

}
