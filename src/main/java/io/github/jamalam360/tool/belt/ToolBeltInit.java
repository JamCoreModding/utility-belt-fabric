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

package io.github.jamalam360.tool.belt;

import io.github.jamalam360.jamlib.config.JamLibConfig;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.jamlib.network.JamLibServerNetworking;
import io.github.jamalam360.jamlib.registry.JamLibRegistry;
import io.github.jamalam360.tool.belt.config.ToolBeltConfig;
import io.github.jamalam360.tool.belt.registry.ItemRegistry;
import io.github.jamalam360.tool.belt.registry.Networking;
import io.github.jamalam360.tool.belt.registry.TrinketsBehaviours;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class ToolBeltInit implements ModInitializer {
    public static final String MOD_ID = "toolbelt";
    private static final JamLibLogger LOGGER = JamLibLogger.getLogger("Tool Belt");

    public static final Map<PlayerEntity, Boolean> TOOL_BELT_SELECTED = new Object2BooleanArrayMap<>();
    public static final Map<PlayerEntity, Integer> TOOL_BELT_SELECTED_SLOTS = new Object2IntArrayMap<>();
    public static final TagKey<Item> ALLOWED_IN_TOOL_BELT = TagKey.of(Registry.ITEM_KEY, idOf("allowed_in_tool_belt"));

    @Override
    public void onInitialize() {
        JamLibRegistry.register(ItemRegistry.class);
        JamLibConfig.init(MOD_ID, ToolBeltConfig.class);
        Networking.setHandlers();
        TrinketsBehaviours.registerEvents();
        JamLibServerNetworking.registerHandlers(MOD_ID);
        LOGGER.logInitialize();
    }

    public static Identifier idOf(String path) {
        return new Identifier(MOD_ID, path);
    }
}