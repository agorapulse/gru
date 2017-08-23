package heist

class MoonService {

    private Map<String, Map> moons = ['earth:moon': [name: 'Moon', planet: 'Earth', created: new Date()]]

    Map findByName(String name) {
        return moons.find {it.key.endsWith(":${name.toLowerCase()}")}?.value
    }

    List<Map> findByPlanet(String planet) {
        return moons.findAll { it.key.startsWith(planet.toLowerCase() + ":") }.values().toList()
    }

    Map findByPlanetAndName(String planet, String name) {
        return moons["$planet:$name".toString().toLowerCase()]
    }

    Map createMoonForPlanet(String planet, Map moon) {
        Map newMoon = new LinkedHashMap(moon)
        newMoon.created = new Date()
        moons["$planet:$moon.name".toString().toLowerCase()] = newMoon
        return moon
    }

}
