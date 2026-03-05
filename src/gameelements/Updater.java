package gameelements;

import exceptions.InsufficientResourcesException;
import exceptions.MaxLevelReachedException;

// Interface for game entities that support level-based upgrades.
public interface Updater {

    void upgrade() throws MaxLevelReachedException, InsufficientResourcesException;
}
