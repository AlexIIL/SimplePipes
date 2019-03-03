package alexiil.mc.mod.pipes.blocks;

import javax.annotation.Nonnull;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.AttributeCombinable;
import alexiil.mc.lib.attributes.SearchParamDirectional;

public abstract class TileBase extends BlockEntity {

    public TileBase(BlockEntityType<?> blockEntityType_1) {
        super(blockEntityType_1);
    }

    @Nonnull
    public <T> T getNeighbourAttribute(AttributeCombinable<T> attr, Direction dir) {
        return attr.get(getWorld(), getPos().offset(dir), SearchParamDirectional.of(dir));
    }

    public DefaultedList<ItemStack> removeItemsForDrop() {
        return DefaultedList.create();
    }
}
