package gameelements;

import exceptions.InsufficientResourcesException;
import exceptions.MaxLevelReachedException;

/**
 * Interface for game entities that can be upgraded to higher levels.
 * <p>
 * Buildings and some unit types implement this interface to support
 * level-based progression. Each upgrade improves the entity's stats
 * but requires resources and is bounded by a maximum level.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public interface Updater {

    /**
     * Upgrades this entity to the next level.
     * <p>
     * Implementations must check resource availability and the current level
     * before performing the upgrade.
     * </p>
     *
     * @throws MaxLevelReachedException        if the entity is already at its maximum level
     * @throws InsufficientResourcesException  if the player cannot afford the upgrade cost
     */
    void upgrade() throws MaxLevelReachedException, InsufficientResourcesException;
}
