package alexiil.mc.mod.pipes.pipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.blocks.TilePipeFluidIron;
import alexiil.mc.mod.pipes.blocks.TilePipeFluidWood;
import alexiil.mc.mod.pipes.blocks.TilePipeSided;
import alexiil.mc.mod.pipes.part.PipeSpBehaviourIron;
import alexiil.mc.mod.pipes.part.SimplePipeParts;
import alexiil.mc.mod.pipes.pipe.PipeSpFlowFluid.SideSection;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.ExactFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public class PipeSpFlowFluid extends PipeSpFlow {

    // Think about fluid packets? (Maybe not for this or even soon but for bc at some point?)
    public static final FluidAmount SECTION_CAPACITY = FluidAmount.BUCKET.div(2);
    private static final FluidAmount AMOUNT_OVERFLOW = FluidAmount.of(1, 1000);

    private final Map<Direction, SideSection> sideSections = new EnumMap<>(Direction.class);
    public final CenterSection centerSection = new CenterSection();

    final FluidInsertable[] insertables;
    long lastTickTime;

    public PipeSpFlowFluid(ISimplePipe pipe) {
        super(pipe);

        for (Direction dir : Direction.values()) {
            sideSections.put(dir, new SideSection(dir));
        }

        this.insertables = new FluidInsertable[6];
        for (Direction dir : Direction.values()) {
            insertables[dir.getOpposite().ordinal()] = new FluidInsertable() {

                @Override
                public FluidVolume attemptInsertion(FluidVolume fluid, Simulation simulation) {
                    if (fluid.isEmpty()) {
                        return fluid;
                    }
                    SideSection sideSection = sideSections.get(dir);
                    if (!sideSection.fluid.isEmpty()) {
                        if (
                            sideSection.fluid.amount().isGreaterThanOrEqual(SECTION_CAPACITY)
                                || !sideSection.fluid.canMerge(fluid)
                        ) {
                            return fluid;
                        }
                    }
                    FluidVolume incoming = fluid.copy();
                    FluidVolume inSection = sideSection.fluid.copy();
                    FluidVolume merged = FluidVolume.merge(inSection, incoming);
                    if (merged == null) {
                        return fluid;
                    }
                    FluidVolume excess = FluidVolumeUtil.EMPTY;
                    if (merged.amount().isGreaterThan(SECTION_CAPACITY)) {
                        excess = merged.split(merged.amount().sub(SECTION_CAPACITY));
                    }
                    if (simulation == Simulation.ACTION) {
                        sideSection.fluid = merged;
                    }
                    return excess;
                }

                @Override
                public FluidFilter getInsertionFilter() {
                    return ConstantFluidFilter.ANYTHING;
                }
            };
        }
    }

    @Override
    public void fromTag(NbtCompound tag) {
        NbtCompound inner = tag.getCompound("sides");
        centerSection.fluid = FluidVolume.fromTag(inner.getCompound("c"));
        for (Direction dir : Direction.values()) {
            sideSections.get(dir).fluid = FluidVolume.fromTag(inner.getCompound(dir.getName()));
        }
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        toTag0(tag);
        return tag;
    }

    private void toTag0(NbtCompound tag) {
        NbtCompound inner = new NbtCompound();
        tag.put("sides", inner);
        inner.put("c", centerSection.fluid.toTag());
        for (Direction dir : Direction.values()) {
            inner.put(dir.getName(), sideSections.get(dir).fluid.toTag());
        }
    }

    @Override
    public void fromInitialClientTag(NbtCompound tag) {
        fromTag(tag);
    }

    @Override
    public void toInitialClientTag(NbtCompound tag) {
        toTag0(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        fromInitialClientTag(tag);
    }

    @Override
    public boolean hasInsertable(Direction dir) {
        return pipe.getFluidInsertable(dir) != RejectingFluidInsertable.NULL;
    }

    @Override
    public boolean hasExtractable(Direction dir) {
        return pipe.getFluidExtractable(dir) != EmptyFluidExtractable.NULL;
    }

    public SideSection getSideSection(Direction dir) {
        return sideSections.get(dir);
    }

    @Override
    public void tick() {
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
        FluidAmount max = SECTION_CAPACITY.sub(section.fluid.amount());
        if (!max.isPositive()) {
            return;
        }
        FluidFilter filter = section.fluid.isEmpty() ? ConstantFluidFilter.ANYTHING
            : new ExactFluidFilter(section.fluid.getFluidKey());
        FluidVolume extracted = from.attemptExtraction(filter, max, Simulation.SIMULATE);
        FluidAmount extractedAmount = extracted.amount();
        if (extractedAmount.isNegative()) {
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
        if (reallyExtracted.isEmpty() || !reallyExtracted.amount().equals(extractedAmount)) {
            throw new IllegalStateException(
                "The second call to attemptExtraction on " + from.getClass()
                    + " returned a different fluid than was expected!\n  first = " + extracted + ",\n  second = "
                    + reallyExtracted
            );
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
    public Object getInsertable(Direction searchDirection) {
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

            if (pipe instanceof PartSpPipe) {
                PartSpPipe part = (PartSpPipe) pipe;
                if (part.behaviour instanceof PipeSpBehaviourIron) {
                    return to == ((PipeSpBehaviourIron) part.behaviour).currentDirection();
                }
                if (part.behaviour instanceof PipeSpBehaviourWood) {
                    return to != ((PipeSpBehaviourWood) part.behaviour).currentDirection();
                }
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
        if (pipe instanceof PartSpPipe) {
            PartSpPipe part = (PartSpPipe) pipe;
            if (part.behaviour instanceof PipeSpBehaviourIron) {
                return to == ((PipeSpBehaviourIron) part.behaviour).currentDirection();
            }
            if (part.behaviour instanceof PipeSpBehaviourWood) {
                return to != ((PipeSpBehaviourWood) part.behaviour).currentDirection();
            }
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
        FluidVolume fluid = FluidVolumeUtil.EMPTY;
        FluidAmount lastTickAmount = FluidAmount.ZERO;

        public void updateAmount() {
            lastTickAmount = fluid.amount();
        }

        FluidAmount getMoveable(Section other, FluidAmount max) {
            FluidAmount inOther = other.lastTickAmount/*.sub(AMOUNT_OVERFLOW)*/.max(FluidAmount.ZERO);
            FluidAmount space = lastTickAmount.sub(inOther).min(SECTION_CAPACITY);
            if (max != null) {
                return space.min(max);
            } else {
                return space;
            }
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
                FluidAmount movable = getMoveable(other, null);
                if (movable.isPositive()) {
                    sides.add(null);
                } else if (!canGoInDirection(null, side) && other.fluid.amount().isLessThan(SECTION_CAPACITY)) {
                    sides.add(null);
                }
            }
            if (canGoInDirection(side, side)) {
                ISimplePipe oPipe = pipe.getNeighbourPipe(side);
                if (oPipe != null && oPipe.getFlow() instanceof PipeSpFlowFluid) {
                    PipeSpFlowFluid oFlow = (PipeSpFlowFluid) oPipe.getFlow();
                    SideSection other = oFlow.sideSections.get(side.getOpposite());
                    FluidAmount movable
                        = lastTickAmount.sub(other.lastTickAmount.sub(AMOUNT_OVERFLOW).max(FluidAmount.ZERO));
                    if (movable.isPositive()) {
                        sides.add(side);
                    }
                } else {
                    FluidInsertable insertable = pipe.getNeighbourAttribute(FluidAttributes.INSERTABLE, side);
                    FluidVolume leftover = insertable.attemptInsertion(fluid, Simulation.SIMULATE);
                    if (leftover.amount().isLessThan(fluid.amount())) {
                        sides.add(side);
                    }
                }
            }

            if (sides.isEmpty()) {
                return;
            }

            Collections.shuffle(sides);

            FluidAmount amt = lastTickAmount.min(fluid.amount());
            FluidAmount[] amounts = amt.splitBalanced(sides.size());

            for (int i = 0; i < sides.size(); i++) {
                Direction to = sides.get(i);
                FluidAmount max = amounts[i];

                if (to == null) {
                    CenterSection other = centerSection;
                    FluidAmount movable = getMoveable(other, max);
                    if (!canGoInDirection(null, side)) {
                        movable = fluid.amount().min(SECTION_CAPACITY.sub(other.fluid.amount()));
                    }
                    if (!movable.isPositive()) {
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
                    ISimplePipe oPipe = pipe.getNeighbourPipe(side);
                    if (oPipe != null && oPipe.getFlow() instanceof PipeSpFlowFluid) {
                        PipeSpFlowFluid oFlow = (PipeSpFlowFluid) oPipe.getFlow();
                        SideSection other = oFlow.sideSections.get(side.getOpposite());
                        FluidAmount movable = getMoveable(other, max);
                        if (!movable.isPositive()) {
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
                        FluidAmount movable = fluid.amount();
                        if (!movable.isPositive()) {
                            continue;
                        }
                        FluidVolume fluidCopy = fluid.copy();
                        FluidVolume split = fluidCopy.split(movable);
                        FluidVolume leftover = insertable.attemptInsertion(split, Simulation.ACTION);
                        FluidAmount inserted = split.amount().sub(leftover.amount());
                        if (inserted.isPositive()) {
                            FluidVolume merged = FluidVolume.merge(fluidCopy, leftover);
                            if (merged == null) {
                                throw new IllegalStateException(
                                    "The fluid " + fluidCopy.getClass() + " and " + leftover.getClass()
                                        + " didn't merge again after they were split!\n"
                                        + "This is either a bug in that fluid volume class, or a bug in "
                                        + insertable.getClass()
                                        + "for returning an invalid result from attemptInsertion"
                                );
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

    public class CenterSection extends Section {

        public FluidVolume getFluid() {
            return fluid;
        }

        @Override
        void tick() {
            if (fluid.isEmpty()) {
                return;
            }
            List<Direction> sides = new ArrayList<>(6);

            for (Direction to : Direction.values()) {
                if (canGoInDirection(null, to)) {
                    SideSection other = sideSections.get(to);
                    FluidAmount movable = getMoveable(other, null);
                    if (movable.isPositive()) {
                        sides.add(to);
                    } else if (!canGoInDirection(to, null) && other.fluid.amount().isLessThan(SECTION_CAPACITY)) {
                        sides.add(to);
                    }
                }
            }

            if (sides.isEmpty()) {
                return;
            }

            Collections.shuffle(sides);

            FluidAmount amt = lastTickAmount.min(fluid.amount());
            FluidAmount[] amounts = amt.splitBalanced(sides.size());

            for (int i = 0; i < sides.size(); i++) {
                Direction to = sides.get(i);
                FluidAmount max = amounts[i];
                SideSection other = sideSections.get(to);
                FluidAmount movable = getMoveable(other, max);
                if (!canGoInDirection(to, null)) {
                    movable = fluid.amount().min(SECTION_CAPACITY.sub(other.fluid.amount()));
                }
                if (!movable.isPositive()) {
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
