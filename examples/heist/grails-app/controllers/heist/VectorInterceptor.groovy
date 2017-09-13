package heist

import org.springframework.http.HttpStatus


class VectorInterceptor {

    VectorMessage vectorMessage

    VectorInterceptor() {
        match(uri: "/api/**")
    }

    boolean before() {
        response.status = HttpStatus.NOT_FOUND.value()
        response.addHeader('X-Message', vectorMessage ? vectorMessage.message : "Vector was here")
        return false
    }

    boolean after() { true } // $COVERAGE-IGNORE$

    void afterView() {
        // no-op
    }
}
