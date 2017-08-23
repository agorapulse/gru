package heist

import org.springframework.http.HttpStatus


class VectorInterceptor {

    VectorInterceptor() {
        match(uri: "/api/**")
    }

    boolean before() {
        response.status = HttpStatus.NOT_FOUND.value()
        response.addHeader('X-Message', "Vector was here")
        return false
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
