package io.github.jamalam360.utility.belt.item;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.inventory.Inventory;

/**
 * @author Jamalam
 */
public interface InventoryComponent<T extends Inventory> extends ComponentV3 {
    T getInventory();
}
