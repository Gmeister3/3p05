package gameelements;

// Abstract base for worker habitants; produces resources via work() each tick.
public abstract class Peasant extends Habitant {

    protected double production;
    protected int level;

    protected Peasant(String name, double production) {
        super(name);
        this.production = production;
        this.level      = 1;
    }

    public abstract Resource work();

    public double getProduction() {
        return production;
    }

    // Effective production is scaled by level.
    public double getEffectiveProduction() {
        return production * level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public double damage() {
        return 1.0;
    }

    @Override
    public void newOperation() {
        // Peasants do not attack; work() is their primary operation
    }

    @Override
    public String toString() {
        return String.format("%s[level=%d, prod=%.1f]",
                getClass().getSimpleName(), level, production);
    }
}
