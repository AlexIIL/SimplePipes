package alexiil.mc.mod.pipes.pipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

import alexiil.mc.mod.pipes.client.model.part.PipeSpPartKey;

import alexiil.mc.lib.multipart.api.AbstractPart.ItemDropTarget;

public class PipeSpBehaviour {

    public final PartSpPipe pipe;

    public PipeSpBehaviour(PartSpPipe pipe) {
        this.pipe = pipe;
    }

    public void fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {

    }

    public NbtCompound toNbt(RegistryWrapper.WrapperLookup lookup) {
        return new NbtCompound();
    }

    public boolean canConnect(Direction dir) {
        return pipe.flow.canConnect(dir);
    }

    protected PipeSpPartKey createModelState() {
        return new PipeSpPartKey(pipe.definition, pipe.encodeConnectedSides());
    }

    public void tick() {

    }

    public ActionResult onUse(PlayerEntity player, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    public ItemActionResult onUseWithItem(ItemStack stack, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void addDrops(ItemDropTarget target, LootContextParameterSet context) {

    }

    public void transform(DirectionTransformation transform) {

    }
}
