package gameelements;

// Interface for entities (fighters and defensive buildings) capable of dealing damage.
public interface Damager {

    double damage();

    // Performs or refreshes this entity's attack operation.
    void newOperation();
}
