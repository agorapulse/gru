package com.agorapulse.gru.spring.heist

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody


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

}
