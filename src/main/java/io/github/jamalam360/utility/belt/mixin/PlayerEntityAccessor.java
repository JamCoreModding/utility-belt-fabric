package io.github.jamalam360.utility.belt.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Jamalam
 */

@Mixin(PlayerEntity.class)
public interface PlayerEntityAccessor {
    @Accessor
    void setSelectedItem(ItemStack stack);
}
