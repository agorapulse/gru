package heist

import org.springframework.http.HttpMethod

class ApiUrlMappings {

    static mappings = {
        "/api/v1/moons/$planet"(controller: "moon", action: "all", method: HttpMethod.GET)
        "/api/v1/moons/$planet"(controller: "moon", action: "create", method: HttpMethod.POST)
        "/api/v1/moons/$planet/$moon"(controller: "moon", action: "moon", method: HttpMethod.GET)
        "/api/v1/moons/$planet/$moon"(controller: "moon", action: "steal", method: HttpMethod.DELETE)
    }
}
