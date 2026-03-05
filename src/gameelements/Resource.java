package gameelements;

// Abstract base for Gold, Iron, and Lumber resources.
public abstract class Resource {

    protected double quantity;

    protected Resource(double quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public void add(double amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    public void subtract(double amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }

    public boolean hasEnough(double required) {
        return this.quantity >= required;
    }

    public abstract String getResourceName();

    @Override
    public String toString() {
        return String.format("%s: %.1f", getResourceName(), quantity);
    }
}
