package heist.micronaut

import spock.lang.Specification

class SanitySpec extends Specification {

    void 'sanity spec'() {
        when:
            Application.main()
        then:
            Application.context
        when:
            try {
                Application.context.stop()
            } catch (IllegalStateException ignored) {
                // application is already stopping
            }
        then:
            noExceptionThrown()
    }

}
