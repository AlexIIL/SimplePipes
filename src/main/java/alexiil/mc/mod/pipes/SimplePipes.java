/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes;

import net.fabricmc.api.ModInitializer;

import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;

public class SimplePipes implements ModInitializer {

    public static final String MODID = "simple_pipes";

    @Override
    public void onInitialize() {
        SimplePipeBlocks.load();
        // SimplePipeItems.load();
    }
}
