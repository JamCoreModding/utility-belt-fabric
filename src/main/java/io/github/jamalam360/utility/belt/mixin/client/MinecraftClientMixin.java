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

package io.github.jamalam360.utility.belt.mixin.client;

import io.github.jamalam360.utility.belt.UtilityBeltClientInit;
import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.config.UtilityBeltConfig;
import io.github.jamalam360.utility.belt.registry.Networking;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBind;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Jamalam
 */

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Final
    public GameOptions options;

    @Inject(
            method = "doItemPick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void utilitybelt$disableMiddleClick(CallbackInfo ci) {
        if (UtilityBeltClientInit.hasSwappedToUtilityBelt) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBind;wasPressed()Z"
            )
    )
    private boolean utilitybelt$hijackHotbarKeys(KeyBind instance) {
        boolean wasPressed = instance.wasPressed();

        if (wasPressed && UtilityBeltClientInit.hasSwappedToUtilityBelt && TrinketsUtil.hasUtilityBelt(this.player)) {
            switch (UtilityBeltConfig.hotbarKeyBehaviour) {
                case SWITCH_BACK_TO_HOTBAR -> {
                    UtilityBeltClientInit.hasSwappedToUtilityBelt = false;
                    UtilityBeltInit.UTILITY_BELT_SELECTED.put(this.player, false);
                    Networking.SET_UTILITY_BELT_SELECTED_C2S.send((buf) -> buf.writeBoolean(false));
                }
                case SWITCH_BELT_SLOT -> {
                    for (int i = 0; i < UtilityBeltInit.UTILITY_BELT_SIZE; i++) {
                        if (this.options.hotbarKeys[i] == instance) {
                            UtilityBeltClientInit.utilityBeltSelectedSlot = i;
                            Networking.SET_UTILITY_BELT_SELECTED_SLOT_C2S.send((buf) -> buf.writeInt(UtilityBeltClientInit.utilityBeltSelectedSlot));
                            UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.put(MinecraftClient.getInstance().player, UtilityBeltClientInit.utilityBeltSelectedSlot);
                            return false;
                        }
                    }
                }
            }

            return true;
        }

        return wasPressed;
    }
}
