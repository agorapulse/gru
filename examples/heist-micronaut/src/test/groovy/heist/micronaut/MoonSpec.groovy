package heist.micronaut

import spock.lang.Specification

import java.time.Instant

class MoonSpec extends Specification {

    void 'sanity spec'() {
        when:
            Moon moon = new Moon()
            moon.planet = 'Pluto'
            moon.name = 'Charon'
        then:
            moon.created.isBefore(Instant.now())
            moon.planet == 'Pluto'
            moon.name == 'Charon'
    }

}
