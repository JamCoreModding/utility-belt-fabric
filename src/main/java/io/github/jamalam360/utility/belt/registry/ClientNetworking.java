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

package io.github.jamalam360.utility.belt.registry;

import io.github.jamalam360.utility.belt.UtilityBeltClientInit;
import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.client.tutorial.SwitchToBeltStage;
import io.github.jamalam360.utility.belt.client.tutorial.SwitchToBeltStage.Type;
import io.github.jamalam360.utility.belt.item.UtilityBeltItem;
import io.github.jamalam360.utility.belt.util.SimplerInventory;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

/**
 * @author Jamalam
 */
public class ClientNetworking {
	/*
	 * Called client side
	 */

	public static void setHandlers() {
		Networking.SWING_HAND
				.setHandler((client, handler, buf, responseSender) -> client.execute(() -> {
					// workaround for mythic metals null pointer. make sure camera has been initialized before swinging hand
					if (MinecraftClient.getInstance().getEntityRenderDispatcher().camera != null) {
						client.player.swingHand(Hand.MAIN_HAND);
					}
				}));
		Networking.SET_UTILITY_BELT_SELECTED_S2C.setHandler((client, handler, buf,
		                                                     responseSender) -> UtilityBeltClientInit.hasSwappedToUtilityBelt = buf.readBoolean());
		Networking.SET_UTILITY_BELT_SELECTED_SLOT_S2C.setHandler((client, handler, buf,
		                                                          responseSender) -> UtilityBeltClientInit.utilityBeltSelectedSlot = buf.readInt());
		Networking.SYNC_UTILITY_BELT_INVENTORY.setHandler((client, handler, buf, responseSender) -> {
			NbtCompound comp = buf.readNbt();

			client.execute(() -> {
				ItemStack utilityBelt = TrinketsUtil.getUtilityBelt(client.player);

				if (utilityBelt != null) {
					SimplerInventory inv = new SimplerInventory(UtilityBeltInit.UTILITY_BELT_SIZE);
					inv.readNbtList(comp.getList("Inventory", 10));
					UtilityBeltItem.update(utilityBelt, inv);
				}
			});
		});
		Networking.ON_MOVE_PICKAXE_TO_BELT.setHandler(((client, handler, buf, responseSender) -> client.execute(() -> {
			if (UtilityBeltTutorial.TUTORIAL.getCurrentStage() instanceof SwitchToBeltStage stage && stage.shouldTrigger(Type.INSERT_PICKAXE)) {
				UtilityBeltTutorial.TUTORIAL.advanceStage();
			}
		})));
	}
}
