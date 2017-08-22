package heist

import grails.converters.JSON
import org.springframework.http.HttpStatus

class MoonController {

    def index() {
        render([foo: "bar"] as JSON)
    }

    def teapot() {
        render status: HttpStatus.I_AM_A_TEAPOT
    }

    def redirectToTeapot() {
        redirect action: 'teapot'
    }

    def forwardToTeapot() {
        forward action: 'teapot'
    }

    def echo() {
        render(request.JSON as JSON)
    }


}
