package alexiil.mc.mod.pipes.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.ExactFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.mod.pipes.blocks.PipeFlowFluid.CenterSection;
import alexiil.mc.mod.pipes.blocks.PipeFlowFluid.Section;
import alexiil.mc.mod.pipes.blocks.PipeFlowFluid.SideSection;
import alexiil.mc.mod.pipes.part.PipeFlow;

public class PipeFlowFluid extends PipeFlow {

    // Think about fluid packets? (Maybe not for this or even soon but for bc at some point?)
    public static final int SECTION_CAPACITY = FluidVolume.BUCKET / 2;

    private final Map<Direction, SideSection> sideSections = new EnumMap<>(Direction.class);
    private final CenterSection centerSection = new CenterSection();

    final FluidInsertable[] insertables;
    long lastTickTime;

    public PipeFlowFluid(TilePipe pipe) {
        super(pipe);

        for (Direction dir : Direction.values()) {
            sideSections.put(dir, new SideSection(dir));
        }

        this.insertables = new FluidInsertable[6];
        for (Direction dir : Direction.values()) {
            insertables[dir.getOpposite().ordinal()] = new FluidInsertable() {

                @Override
                public FluidVolume attemptInsertion(FluidVolume fluid, Simulation simulation) {
                    return fluid;
                }

                @Override
                public FluidFilter getInsertionFilter() {
                    return ConstantFluidFilter.ANYTHING;
                }
            };
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        CompoundTag inner = tag.getCompound("sides");
        centerSection.fluid = FluidVolume.fromTag(inner.getCompound("c"));
        for (Direction dir : Direction.values()) {
            sideSections.get(dir).fluid = FluidVolume.fromTag(inner.getCompound(dir.getName()));
        }
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        toTag0(tag);
        return tag;
    }

    private void toTag0(CompoundTag tag) {
        CompoundTag inner = new CompoundTag();
        tag.put("sides", inner);
        inner.put("c", centerSection.fluid.toTag());
        for (Direction dir : Direction.values()) {
            inner.put(dir.getName(), sideSections.get(dir).fluid.toTag());
        }
    }

    @Override
    protected void fromInitialClientTag(CompoundTag tag) {
        fromTag(tag);
    }

    @Override
    public void toInitialClientTag(CompoundTag tag) {
        toTag0(tag);
    }

    @Override
    protected void fromClientTag(CompoundTag tag) {
        fromInitialClientTag(tag);
    }

    @Override
    protected boolean canConnect(Direction dir) {
        return pipe.getFluidInsertable(dir) != RejectingFluidInsertable.NULL;
    }

    @Override
    protected void tick() {
        if (world().isClient) {
            return;
        }

        lastTickTime = world().getTime();

        updateAmounts();

        centerSection.tick();
        for (SideSection section : sideSections.values()) {
            section.tick();
        }

        updateAmounts();

        if (Math.random() < 0.1) {
            pipe.sendFlowPacket(toTag());
        }
    }

    public void tryExtract(Direction dir) {
        FluidExtractable from = pipe.getFluidExtractable(dir);
        SideSection section = sideSections.get(dir);
        int max = SECTION_CAPACITY - section.fluid.getAmount();
        if (max <= 0) {
            return;
        }
        FluidFilter filter =
            section.fluid.isEmpty() ? ConstantFluidFilter.ANYTHING : new ExactFluidFilter(section.fluid.getFluidKey());
        FluidVolume extracted = from.attemptExtraction(filter, max, Simulation.SIMULATE);
        int extractedAmount = extracted.getAmount();
        if (extractedAmount < 0) {
            throw new IllegalStateException("Extracted a negative amount of fluid!");
        }
        if (extracted.isEmpty()) {
            return;
        }
        FluidVolume merged = section.fluid.isEmpty() ? extracted : FluidVolume.merge(section.fluid.copy(), extracted);
        if (merged == null) {
            return;
        }
        // Just to speed up attemptExtraction
        // Note that making the filter more specific shouldn't change the result
        if (filter == ConstantFluidFilter.ANYTHING) {
            filter = new ExactFluidFilter(extracted.getFluidKey());
        }
        FluidVolume reallyExtracted = from.attemptExtraction(filter, extractedAmount, Simulation.ACTION);
        if (reallyExtracted.isEmpty() || reallyExtracted.getAmount() != extractedAmount) {
            throw new IllegalStateException("The second call to attemptExtraction on " + from.getClass()
                + " returned a different fluid than was expected!\n  first = " + extracted + ",\n  second = "
                + reallyExtracted);
        }
        FluidVolume reallyMerged = FluidVolume.merge(section.fluid, reallyExtracted);
        if (reallyMerged == null) {
            throw new IllegalStateException("Failed to merge back again!");
        }
        section.fluid = reallyMerged;
    }

    private void updateAmounts() {
        centerSection.updateAmount();
        for (SideSection section : sideSections.values()) {
            section.updateAmount();
        }
    }

    @Override
    protected Object getInsertable(Direction searchDirection) {
        return insertables[searchDirection.ordinal()];
    }

    private boolean canGoInDirection(@Nullable Direction from, @Nullable Direction to) {
        if (from == null) {
            if (to == null) {
                throw new IllegalArgumentException("You cannot got from the center to the center!");
            }
            if (!pipe.isConnected(to)) {
                return false;
            }
            if (pipe instanceof TilePipeFluidIron) {
                return to == ((TilePipeSided) pipe).currentDirection();
            }
            if (pipe instanceof TilePipeFluidWood) {
                return to != ((TilePipeSided) pipe).currentDirection();
            }
            return true;
        }
        if (to == null) {
            return true;
        }
        if (to != from) {
            throw new IllegalArgumentException("You cannot got from a side to another side except for itself!");
        }
        if (!pipe.isConnected(to)) {
            return false;
        }
        // Can send to direction
        if (pipe instanceof TilePipeFluidIron) {
            return to == ((TilePipeSided) pipe).currentDirection();
        }
        if (pipe instanceof TilePipeFluidWood) {
            return to != ((TilePipeSided) pipe).currentDirection();
        }
        return true;
    }

    @Environment(EnvType.CLIENT)
    public FluidVolume getClientCenterFluid() {
        return centerSection.fluid;
    }

    @Environment(EnvType.CLIENT)
    public FluidVolume getClientSideFluid(Direction side) {
        return sideSections.get(side).fluid;
    }

    abstract class Section {
        FluidVolume fluid = FluidKeys.EMPTY.withAmount(0);
        int lastTickAmount = 0;

        public void updateAmount() {
            lastTickAmount = fluid.getAmount();
        }

        abstract void tick();
    }

    class SideSection extends Section {
        final Direction side;

        public SideSection(Direction side) {
            this.side = side;
        }

        @Override
        void tick() {
            if (fluid.isEmpty()) {
                return;
            }
            List<Direction> sides = new ArrayList<>(2);
            if (canGoInDirection(side, null)) {
                CenterSection other = centerSection;
                int movable = (lastTickAmount - other.lastTickAmount);
                if (movable > 0) {
                    sides.add(null);
                } else if (!canGoInDirection(null, side) && other.fluid.getAmount() < SECTION_CAPACITY) {
                    sides.add(null);
                }
            }
            if (canGoInDirection(side, side)) {
                TilePipe oPipe = pipe.getNeighbourPipe(side);
                if (oPipe != null && oPipe.flow instanceof PipeFlowFluid) {
                    PipeFlowFluid oFlow = (PipeFlowFluid) oPipe.flow;
                    SideSection other = oFlow.sideSections.get(side.getOpposite());
                    int movable = (lastTickAmount - other.lastTickAmount);
                    if (movable > 0) {
                        sides.add(side);
                    }
                } else {
                    FluidInsertable insertable = pipe.getNeighbourAttribute(FluidAttributes.INSERTABLE, side);
                    FluidVolume leftover = insertable.attemptInsertion(fluid, Simulation.SIMULATE);
                    if (leftover.getAmount() < fluid.getAmount()) {
                        sides.add(side);
                    }
                }
            }

            Collections.shuffle(sides);

            for (Direction to : sides) {
                if (to == null) {
                    CenterSection other = centerSection;
                    int movable = (lastTickAmount - other.lastTickAmount + sides.size() - 1) / sides.size();
                    if (!canGoInDirection(null, side)) {
                        movable = Math.min(fluid.getAmount(), SECTION_CAPACITY - other.fluid.getAmount());
                    }
                    if (movable < 1) {
                        continue;
                    }
                    FluidVolume fluidCopy = fluid.copy();
                    FluidVolume split = fluidCopy.split(movable);
                    FluidVolume merged = FluidVolume.merge(other.fluid, split);
                    if (merged != null) {
                        other.fluid = merged;
                        fluid = fluidCopy;
                        if (fluid.isEmpty()) {
                            return;
                        }
                    }
                } else {
                    TilePipe oPipe = pipe.getNeighbourPipe(side);
                    if (oPipe != null && oPipe.flow instanceof PipeFlowFluid) {
                        PipeFlowFluid oFlow = (PipeFlowFluid) oPipe.flow;
                        SideSection other = oFlow.sideSections.get(side.getOpposite());
                        int movable = (lastTickAmount - other.lastTickAmount + sides.size() - 1) / sides.size();
                        if (movable < 1) {
                            continue;
                        }
                        FluidVolume fluidCopy = fluid.copy();
                        FluidVolume split = fluidCopy.split(movable);
                        FluidVolume merged = FluidVolume.merge(other.fluid, split);
                        if (merged != null) {
                            other.fluid = merged;
                            fluid = fluidCopy;
                            if (fluid.isEmpty()) {
                                return;
                            }
                        }
                    } else {
                        FluidInsertable insertable = pipe.getNeighbourAttribute(FluidAttributes.INSERTABLE, side);
                        int movable = (fluid.getAmount() + 1) / 2;
                        if (movable < 0) {
                            continue;
                        }
                        FluidVolume fluidCopy = fluid.copy();
                        FluidVolume split = fluidCopy.split(movable);
                        FluidVolume leftover = insertable.attemptInsertion(split, Simulation.ACTION);
                        int inserted = split.getAmount() - leftover.getAmount();
                        if (inserted > 0) {
                            FluidVolume merged = FluidVolume.merge(fluidCopy, leftover);
                            if (merged == null) {
                                throw new IllegalStateException("The fluid " + fluidCopy.getClass() + " and "
                                    + leftover.getClass() + " didn't merge again after they were split!\n"
                                    + "This is either a bug in that fluid volume class, or a bug in "
                                    + insertable.getClass() + "for returning an invalid result from attemptInsertion");
                            }
                            fluid = merged;
                            if (fluid.isEmpty()) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    class CenterSection extends Section {

        @Override
        void tick() {
            if (fluid.isEmpty()) {
                return;
            }
            List<Direction> sides = new ArrayList<>(6);

            for (Direction to : Direction.values()) {
                if (canGoInDirection(null, to)) {
                    SideSection other = sideSections.get(to);
                    int movable = (lastTickAmount - other.lastTickAmount);
                    if (movable > 0) {
                        sides.add(to);
                    } else if (!canGoInDirection(to, null) && other.fluid.getAmount() < SECTION_CAPACITY) {
                        sides.add(to);
                    }
                }
            }

            Collections.shuffle(sides);

            for (Direction to : sides) {
                SideSection other = sideSections.get(to);
                int movable = (lastTickAmount - other.lastTickAmount + sides.size() - 1) / sides.size();
                if (!canGoInDirection(to, null)) {
                    movable = Math.min(fluid.getAmount(), SECTION_CAPACITY - other.fluid.getAmount());
                }
                if (movable < 1) {
                    continue;
                }
                FluidVolume fluidCopy = fluid.copy();
                FluidVolume split = fluidCopy.split(movable);
                FluidVolume merged = FluidVolume.merge(other.fluid, split);
                if (merged != null) {
                    other.fluid = merged;
                    fluid = fluidCopy;
                    if (fluid.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }
}
