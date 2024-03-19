/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
