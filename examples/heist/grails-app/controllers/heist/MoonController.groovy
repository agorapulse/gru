package heist

import grails.converters.JSON
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

class MoonController {

    MoonService moonService

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

    def modelAndView() {
        new ModelAndView('info', [foo: 'bar'], HttpStatus.ACCEPTED)
    }

    def paramsToJson() {
        render(params as JSON)
    }

    def exceptional() {
        throw new Exception("Something Happened")
    }

    def anything() {
        ['foo', 'bar']
    }

    def moon(String planet, String moon) {
        if (params.info && params.boolean('info')) {
            forward action: 'info', controller: 'moon', params: [planet: planet, moon: moon]
            return
        }

        if (planet == 'earth' && moon == 'noom') {
            if (request.getHeader('Authorization') == 'Felonius') {
                render([name: 'Noom', planet: 'Earth'] as JSON)
                return
            }
            render status: HttpStatus.FORBIDDEN
            return
        }

        if (planet == '-') {
            def item = moonService.findByName(moon)
            if (item) {
                redirect uri: "/moons/${item.planet.toLowerCase()}/${item.name.toLowerCase()}"
                return
            }
        }

        def item = moonService.findByPlanetAndName(planet, moon)
        if (item) {
            render(item as JSON)
            return
        }
        render status: HttpStatus.NOT_FOUND
    }

    def info(String planet, String moon) {
        def item = moonService.findByPlanetAndName(planet, moon)
        if (item) {
            if (request.getHeader('Accept') == 'text/plain') {
                render text: "$item.name goes around $item.planet"
                return
            }

            return [moon: item]
        }
        render status: HttpStatus.NOT_FOUND
    }

    def create(String planet) {
        render moonService.createMoonForPlanet(planet, request.JSON as Map)
    }

    def steal(String planet, String moon) {
        if (params.with == 'shrink-ray') {
            render status: HttpStatus.NO_CONTENT
            return
        }
        render status: HttpStatus.BAD_REQUEST
    }

    def all(String planet) {
        render(moonService.findByPlanet(planet) as JSON)
    }

    def postWithMessageAndImage() {
        MultipartFile file = request.getFile('theFile')
        render([
                message: params.message,
                size: file.bytes.length,
                contentType: file.contentType,
                filename: file.originalFilename
        ] as JSON)
    }


}
