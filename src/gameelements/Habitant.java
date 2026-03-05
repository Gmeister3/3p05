package gameelements;

// Abstract base for all village inhabitants (fighters and peasants).
public abstract class Habitant implements Damager {

    protected final String name;

    protected Habitant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + name + "]";
    }
}
