package com.agorapulse.gru.spring.heist

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


@Controller
@RequestMapping('/moons')
class MoonController {

    @RequestMapping(value = '/{planet}/{moon}', method=RequestMethod.GET)
    public @ResponseBody Moon sayHello(@PathVariable("planet") String planet, @PathVariable('moon') String moon) {
        return new Moon(name: 'Moon', planet: 'Earth')
    }

}
