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

package io.github.jamalam360.tool.belt.mixin.input;

import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import io.github.jamalam360.tool.belt.registry.ToolBeltClientNetworking;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author Jamalam
 */

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(
            method = "onMouseScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true
    )
    private void toolbelt$beforeMouseScrollEvent(long window, double scrollDeltaX, double scrollDeltaY, CallbackInfo ci, double amount) {
        if (ToolBeltClientInit.hasSwappedToToolBelt) {
            if (amount > 0) {
                ToolBeltClientInit.toolBeltSelectedSlot--;
                if (ToolBeltClientInit.toolBeltSelectedSlot < 0) {
                    ToolBeltClientInit.toolBeltSelectedSlot = 3;
                }
            } else if (amount < 0) {
                ToolBeltClientInit.toolBeltSelectedSlot++;
                if (ToolBeltClientInit.toolBeltSelectedSlot >= 4) {
                    ToolBeltClientInit.toolBeltSelectedSlot = 0;
                }
            }

            if (amount != 0) {
                ToolBeltClientNetworking.SET_TOOL_BELT_SELECTED_SLOT.send((buf) -> buf.writeInt(ToolBeltClientInit.toolBeltSelectedSlot));
            }

            ci.cancel();
        }
    }
}
