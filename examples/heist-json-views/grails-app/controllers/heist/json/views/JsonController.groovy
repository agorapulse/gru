package heist.json.views

import grails.converters.JSON

class JsonController {

    def index() {
         [moons: [[name: 'Moon', planet: 'Earth']]]
    }

    def show() {
        if (params.manual) {
            render([name: 'Moon', planet: 'Earth'] as JSON)
            return
        }
        [moon: [name: 'Moon', planet: 'Earth']]
    }

    def missing() {
        [moon: [name: 'Moon', planet: 'Earth']]
    }

}
