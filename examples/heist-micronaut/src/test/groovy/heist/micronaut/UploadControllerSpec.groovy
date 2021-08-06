package heist.micronaut

import com.agorapulse.gru.Gru
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
class UploadControllerSpec extends Specification {

    @AutoCleanup @Inject Gru gru

    void 'upload file'() {
        expect:
            gru.test {
                post '/upload', {
                    upload {
                        file 'theFile', 'hello.txt', inline('Hello World'), 'text/plain'
                    }
                }
                expect {
                    text inline('11')
                }
            }
    }


}
