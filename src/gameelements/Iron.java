package gameelements;

// Iron resource; used for defensive buildings and heavy units.
public class Iron extends Resource {

    public Iron(double quantity) {
        super(quantity);
    }

    @Override
    public String getResourceName() {
        return "Iron";
    }
}
