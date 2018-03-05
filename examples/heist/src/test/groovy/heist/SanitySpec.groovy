package heist

import com.agorapulse.gru.grails.minions.jsonview.JsonViewSupport
import grails.boot.GrailsApp
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

import javax.servlet.ServletContext

/**
 * Trying to test untestable.
 */
class SanitySpec extends Specification {

    @ConfineMetaClassChanges(GrailsApp)
    void 'mock run app'() {
        when:
            Application.main()
        then:
            Application.context
        when:
            Application.context.stop()
        then:
            noExceptionThrown()
    }

    void 'some stupid calls'() {
        given:
            ServletContext context = Mock(ServletContext)
        expect:
            !new BootStrap().init(context)
            new VectorInterceptor().after()
            !new BootStrap().destroy()
    }

    void 'json tests are disabled if not present on the classpath'() {
        expect:
            !JsonViewSupport.enabled
    }
}
