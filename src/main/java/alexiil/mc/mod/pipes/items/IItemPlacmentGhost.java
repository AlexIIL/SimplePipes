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
