/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemUsageContext;

public interface IItemPlacmentGhost {

    /** @return A new {@link GhostPlacement} to call the appropriate methods on. In particular
     *         {@link GhostPlacement#preRender(ItemUsageContext)} will be called after this to determine if this is
     *         actually still valid. */
    @Nullable
    GhostPlacement createGhostPlacement(ItemUsageContext ctx);
}
