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

package io.github.jamalam360.tool.belt.mixin.client.tutorial;

import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.tutorial.Tutorial;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Jamalam
 */

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
    @Inject(
            method = "onAdvancements",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/network/packet/s2c/play/AdvancementUpdateS2CPacket;getAdvancementsToProgress()Ljava/util/Map;"
                    )
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void toolbelt$tryTriggerTutorial(AdvancementUpdateS2CPacket packet, CallbackInfo ci, Iterator<Map.Entry<Identifier, AdvancementProgress>> var2, Map.Entry<Identifier, AdvancementProgress> entry, Advancement advancement, AdvancementProgress advancementProgress) {
        if (advancement.getId().equals(ToolBeltInit.idOf("obtain_tool_belt")) && !packet.shouldClearCurrent() && advancementProgress.isDone()) {
            Tutorial.startTutorial();
        }
    }
}
