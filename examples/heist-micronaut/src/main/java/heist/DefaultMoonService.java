package heist;

import javax.inject.Singleton;

@Singleton
public class DefaultMoonService implements MoonService {

    @Override
    public Moon get(String planet, String name) {
        return new Moon(planet, name);
    }
}
