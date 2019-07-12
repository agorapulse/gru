package heist

import org.springframework.http.HttpMethod

class UrlMappings {

    static mappings = {
        "/moons/give-me-some-model"(controller: "moon", action: "modelAndView", method: HttpMethod.GET)
        "/moons/give-me-anything"(controller: "moon", action: "anything", method: HttpMethod.GET)
        "/moons/params-to-json"(controller: "moon", action: "paramsToJson", method: HttpMethod.POST)
        "/moons/error"(controller: "moon", action: "exceptional")
        "/moons/upload"(controller: "moon", action: "postWithMessageAndImage")
        "/moons/cookie"(controller: "moon", action: "cookie")
        "/moons/setCookie"(controller: "moon", action: "setCookie")

        "/moons/$planet"(controller: "moon", action: "all", method: HttpMethod.GET)
        "/moons/$planet"(controller: "moon", action: "create", method: HttpMethod.POST)
        "/moons/$planet/$moon"(controller: "moon", action: "moon", method: HttpMethod.GET)
        "/moons/$planet/$moon"(controller: "moon", action: "steal", method: HttpMethod.DELETE)
        "/moons/$planet/$moon/info"(controller: "moon", action: "info", method: HttpMethod.GET)

//        "/$controller/$action?/$id?(.$format)?"{
//            constraints {
//                // apply constraints here
//            }
//        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
