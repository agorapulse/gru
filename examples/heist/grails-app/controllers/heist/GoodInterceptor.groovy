package heist

class GoodInterceptor {

    GoodInterceptor() {
        match(uri: '/moons/good/interceptor')
    }

    boolean before() {
        response.addHeader('X-Good-Message', 'You are so good!')
        return true
    }

    boolean after() {
        true
    }

    void afterView() {
        // no-op
    }
}
