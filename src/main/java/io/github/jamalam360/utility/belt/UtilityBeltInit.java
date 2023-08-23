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

package io.github.jamalam360.utility.belt;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.jamalam360.jamlib.config.JamLibConfig;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.jamlib.network.JamLibServerNetworking;
import io.github.jamalam360.jamlib.registry.JamLibRegistry;
import io.github.jamalam360.utility.belt.config.UtilityBeltConfig;
import io.github.jamalam360.utility.belt.item.InventoryComponent;
import io.github.jamalam360.utility.belt.item.ItemInventoryComponent;
import io.github.jamalam360.utility.belt.registry.ItemRegistry;
import io.github.jamalam360.utility.belt.registry.Networking;
import io.github.jamalam360.utility.belt.registry.ScreenHandlerRegistry;
import io.github.jamalam360.utility.belt.registry.TrinketsBehaviours;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;

public class UtilityBeltInit implements ModInitializer, ItemComponentInitializer {

	public static final String MOD_ID = "utilitybelt";
	public static final JamLibLogger LOGGER = JamLibLogger.getLogger(MOD_ID);

	public static final Map<UUID, Boolean> UTILITY_BELT_SELECTED = new Object2BooleanOpenHashMap<>();
	public static final Map<UUID, Integer> UTILITY_BELT_SELECTED_SLOTS = new Object2IntOpenHashMap<>();
	public static final TagKey<Item> ALLOWED_IN_UTILITY_BELT = TagKey.of(Registries.ITEM.getKey(),
			idOf("allowed_in_utility_belt"));
	@SuppressWarnings("rawtypes")
	public static final ComponentKey<InventoryComponent> INVENTORY = ComponentRegistry
			.getOrCreate(idOf("belt_inventory"), InventoryComponent.class);

	/*
	 * We use this now to aid with un-hard-coding in case issue #2 is ever tackled.
	 */
	public static final int UTILITY_BELT_SIZE = 4;

	public static Identifier idOf(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		JamLibRegistry.register(ItemRegistry.class, ScreenHandlerRegistry.class);
		JamLibConfig.init(MOD_ID, UtilityBeltConfig.class);
		TrinketsBehaviours.registerEvents();
		Networking.setHandlers();
		JamLibServerNetworking.registerHandlers(MOD_ID);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ((Ducks.LivingEntity) handler.player).utilitybelt$setUtilityBeltEquipped(TrinketsUtil.hasUtilityBelt(handler.player)));

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			CommandRegistrationCallback.EVENT.register((dispatcher, ctx, dedicated) -> dispatcher.register(
					CommandManager.literal(MOD_ID)
							.then(CommandManager.literal("selected_slots")
									.executes(context -> {
										context.getSource().getPlayer().sendMessage(
												Text.literal(UTILITY_BELT_SELECTED.toString()), false);

										context.getSource().getPlayer().sendMessage(
												Text.literal(UTILITY_BELT_SELECTED_SLOTS.toString()), false);

										return 1;
									}))));
		}

		LOGGER.logInitialize();
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
		registry.register(ItemRegistry.UTILITY_BELT, INVENTORY, ItemInventoryComponent::new);
	}
}
