/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.utility.belt.mixin;

import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.item.ItemInventoryComponent;
import io.github.jamalam360.utility.belt.registry.Networking;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Jamalam
 */

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void utilitybelt$switchBackToHotbar(CallbackInfo ci) {
        if (!((PlayerEntity) (Object) this).world.isClient) {
            UtilityBeltInit.UTILITY_BELT_SELECTED.put(((PlayerEntity) (Object) this).getUuid(), false);
            Networking.SET_UTILITY_BELT_SELECTED_S2C.send((ServerPlayerEntity) (Object) this,
                  (buf) -> buf.writeBoolean(false));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void utilitybelt$syncInventoryIfNeeded(CallbackInfo ci) {
        if (((PlayerEntity) (Object) this) instanceof ServerPlayerEntity serverPlayerEntity) {
            if (TrinketsUtil.hasUtilityBelt(serverPlayerEntity)) {
                ItemStack stack = TrinketsUtil.getUtilityBelt(serverPlayerEntity);
                ((ItemInventoryComponent) UtilityBeltInit.INVENTORY.get(stack)).syncToClientIfDirty(serverPlayerEntity);
            }
        }
    }
}
