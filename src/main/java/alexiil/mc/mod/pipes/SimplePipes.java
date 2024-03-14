/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;
import alexiil.mc.mod.pipes.container.SimplePipeContainers;
import alexiil.mc.mod.pipes.items.SimplePipeItems;
import alexiil.mc.mod.pipes.part.FacadeStateManager;
import alexiil.mc.mod.pipes.part.SimplePipeParts;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;

public class SimplePipes implements ModInitializer {

    public static final String MODID = "simple_pipes";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info("[SimplePipes] Initializing...");

        FacadeStateManager.load();
        SimplePipeParts.load();
        SimplePipeBlocks.load();
        SimplePipeItems.load();
        SimplePipeContainers.load();
        PartSpPipe.load();

        LOGGER.info("[SimplePipes] Mod Initialized.");
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }
}
