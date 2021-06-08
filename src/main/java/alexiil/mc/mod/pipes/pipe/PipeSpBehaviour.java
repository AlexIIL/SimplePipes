package alexiil.mc.mod.pipes.pipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.blocks.TilePipe.PipeBlockModelState;

import alexiil.mc.lib.multipart.api.AbstractPart.ItemDropTarget;

public class PipeSpBehaviour {

    public final PartSpPipe pipe;

    public PipeSpBehaviour(PartSpPipe pipe) {
        this.pipe = pipe;
    }

    public void fromNbt(NbtCompound nbt) {

    }

    public NbtCompound toNbt() {
        return new NbtCompound();
    }

    public boolean canConnect(Direction dir) {
        return pipe.flow.canConnect(dir);
    }

    protected PipeBlockModelState createModelState() {
        return new PipeBlockModelState(pipe.definition, pipe.encodeConnectedSides());
    }

    public void tick() {

    }

    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    public void copyFrom(TilePipe oldTile) {

    }

    public void addDrops(ItemDropTarget target, LootContext context) {

    }
}
