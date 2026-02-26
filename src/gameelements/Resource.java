package gameelements;

/**
 * Abstract base class for all in-game resources (Gold, Iron, Lumber).
 * <p>
 * Resources are produced by worker buildings and peasants, and are consumed
 * when constructing buildings, training troops, or upgrading entities.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public abstract class Resource {

    /** The current quantity of this resource. */
    protected double quantity;

    /**
     * Constructs a Resource with the specified initial quantity.
     *
     * @param quantity the initial quantity
     */
    protected Resource(double quantity) {
        this.quantity = Math.max(0, quantity);
    }

    /**
     * Returns the current quantity of this resource.
     *
     * @return the quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Sets the current quantity of this resource.
     *
     * @param quantity the new quantity (must be &ge; 0)
     */
    public void setQuantity(double quantity) {
        this.quantity = Math.max(0, quantity);
    }

    /**
     * Adds the given amount to the current quantity.
     *
     * @param amount the amount to add (must be positive)
     */
    public void add(double amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * Subtracts the given amount from the current quantity, floored at 0.
     *
     * @param amount the amount to subtract
     */
    public void subtract(double amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }

    /**
     * Returns whether the available quantity meets or exceeds the required amount.
     *
     * @param required the amount needed
     * @return {@code true} if there is enough of this resource
     */
    public boolean hasEnough(double required) {
        return this.quantity >= required;
    }

    /**
     * Returns the display name for this resource type.
     *
     * @return the resource name (e.g., "Gold", "Iron", "Lumber")
     */
    public abstract String getResourceName();

    /**
     * Returns a string representation of this resource.
     *
     * @return string in the form {@code ResourceName: quantity}
     */
    @Override
    public String toString() {
        return String.format("%s: %.1f", getResourceName(), quantity);
    }
}
