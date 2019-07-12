package com.agorapulse.gru.spring.heist

import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping('/moons')
class MoonController {

    @RequestMapping(value = '/{planet}/{moon}', method=RequestMethod.GET)
    @ResponseBody Moon sayHello(@PathVariable("planet") String planet, @PathVariable('moon') String moon) {
        return new Moon(name: 'Moon', planet: 'Earth')
    }

    @RequestMapping('/the-only-true-moon')
    String theOnlyTrueMoon() {
        return 'redirect:/moons/earth/moon'
    }

    @RequestMapping(value = '/params-echo', produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody Map paramsEcho(@RequestParam Map params) {
        return params
    }

    @RequestMapping(value = '/headers-echo', produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody Map headersEcho(@RequestHeader Map<String, String> headers) {
        return headers
    }

    @RequestMapping(value = '/json-echo', produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody Map jsonEcho(@RequestBody Map<String, Object> body) {
        return body
    }

    @RequestMapping("/greeting")
    String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name)
        return "greeting"
    }

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Map handleFileUpload(
        @RequestParam("theFile") MultipartFile file,
        @RequestParam("message") String message
    ) {
        return [
            message: message,
            size: file.bytes.length,
            contentType: file.contentType,
            filename: file.originalFilename
        ]
    }

    @RequestMapping(value = '/cookie', method=RequestMethod.GET)
    @ResponseBody Map cookies(HttpServletRequest request) {
        return request.cookies?.collectEntries { [(it.name): it.value] } ?: [:]
    }

    @RequestMapping(value = '/setCookie', method=RequestMethod.GET)
    void setCookie(HttpServletResponse response) {
        response.addCookie(new Cookie('chocolate', 'rules'))
        response.addCookie(new Cookie('coffee', 'lover').with {
            secure = true
            domain = 'localhost'
            it
        })
    }

}
