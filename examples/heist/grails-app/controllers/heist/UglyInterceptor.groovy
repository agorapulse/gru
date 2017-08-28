package heist

class UglyInterceptor {

    UglyInterceptor() {
        match(uri: '/moons/ugly/interceptor')
    }

    boolean before() {
        response.addHeader('X-Bad-Message', 'This will not proceed after run!')
        return true
    }

    boolean after() {
        return false
    }

    void afterView() {
        // no-op
    }
}
