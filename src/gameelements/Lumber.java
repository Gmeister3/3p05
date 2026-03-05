package gameelements;

// Lumber resource; required for buildings and some unit types.
public class Lumber extends Resource {

    public Lumber(double quantity) {
        super(quantity);
    }

    @Override
    public String getResourceName() {
        return "Lumber";
    }
}
