package heist;

import java.time.Instant;

public class Moon {

    public Moon() { }

    public Moon(String name, String planet) {
        this.name = name;
        this.planet = planet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    private String name;
    private String planet;
    private Instant created = Instant.now();

}
