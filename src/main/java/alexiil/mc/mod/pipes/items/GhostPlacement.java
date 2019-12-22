/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.items;

import javax.annotation.Nullable;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;

public abstract class GhostPlacement {

    /** Called before {@link #render(MatrixStack, VertexConsumerProvider, PlayerEntity, float)} to return either:
     * <ol>
     * <li>Itself - to make it the ghost that is rendered</li>
     * <li>A new {@link GhostPlacement} which will be rendered instead</li>
     * <li>Null - if this {@link GhostPlacement} is no longer valid</li>
     * </ol>
     * Note that this must inspect the current {@link ItemStack} in {@link ItemUsageContext#getStack()} to ensure that
     * it is still a valid stack. */
    @Nullable
    public abstract GhostPlacement preRender(ItemUsageContext ctx);

    public abstract void render(MatrixStack matrices, VertexConsumerProvider vcp, PlayerEntity player, float partialTicks);

    public boolean isLockedOpen() {
        return false;
    }

    public void tick() {
        // Do nothing be default
    }

    public void delete() {
        // Do nothing by default
    }
}
