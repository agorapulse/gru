package heist

class BadInterceptor {

    BadInterceptor() {
        match(uri: '/moons/bad/interceptor')
    }

    boolean before() {
        response.addHeader('X-Bad-Message', 'This will fail!')
        return true
    }

    boolean after() {
        throw new RuntimeException("You should expect this!")
    }

    void afterView() {
        // no-op
    }
}
