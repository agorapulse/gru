package heist

import org.springframework.http.HttpMethod

class UrlMappings {

    static mappings = {

        "/moons/$planet"(controller: "moon", action: "all", method: HttpMethod.GET)
        "/moons/$planet"(controller: "moon", action: "create", method: HttpMethod.POST)
        "/moons/$planet/$moon"(controller: "moon", action: "moon", method: HttpMethod.GET)
        "/moons/$planet/$moon"(controller: "moon", action: "steal", method: HttpMethod.DELETE)
        "/moons/$planet/$moon/info"(controller: "moon", action: "info", method: HttpMethod.GET)

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
