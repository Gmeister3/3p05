package gameelements;

// Gold resource; primary currency for buildings, training, and upgrades.
public class Gold extends Resource {

    public Gold(double quantity) {
        super(quantity);
    }

    @Override
    public String getResourceName() {
        return "Gold";
    }
}
