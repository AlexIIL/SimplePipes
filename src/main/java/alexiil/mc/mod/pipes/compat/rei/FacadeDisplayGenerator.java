package alexiil.mc.mod.pipes.compat.rei;

import alexiil.mc.mod.pipes.items.ItemFacade;
import alexiil.mc.mod.pipes.items.SimplePipeItems;
import alexiil.mc.mod.pipes.part.FacadeShape;
import alexiil.mc.mod.pipes.part.FullFacade;
import alexiil.mc.mod.pipes.util.IngredientHelper;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.plugin.common.displays.DefaultStoneCuttingDisplay;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;


public class FacadeDisplayGenerator implements DynamicDisplayGenerator<DefaultStoneCuttingDisplay> {
    private static final Identifier RECIPE_ID = Identifier.of("buildcraftsilicon:facade_generated");

    @Override
    public Optional<List<DefaultStoneCuttingDisplay>> getRecipeFor(EntryStack<?> entry) {
        if (entry.getValue() instanceof ItemStack itemStack) {
            if (!(itemStack.getItem() instanceof ItemFacade)) {
                return Optional.empty();
            }
            FullFacade facade = ItemFacade.getStates(itemStack.copy());
            if (!canCut(facade.state.state)) {
                return Optional.empty();
            }
            List<DefaultStoneCuttingDisplay> list = new ArrayList<>();
            BiConsumer<ItemStack, ItemStack> gen = (input, output) -> {
                Ingredient ing = IngredientHelper.fromStacks(input.copy());
                list.add(new DefaultStoneCuttingDisplay(new RecipeEntry<>(Identifier.of("buildcraftsilicon:facade_generated"), new StonecuttingRecipe("", ing, output.copy()))));
            };
            ItemStack blockStack = facade.state.requiredStack.copy();
            blockStack.setCount(1);

            FacadeShape shape = facade.shape;
            int from = shape.getRecipeMicroVoxelVolume();
            if (4096 / from <= 64) {
                ItemStack output = SimplePipeItems.FACADE.createItemStack(facade);
                output.setCount(4096 / from);
                gen.accept(blockStack, output);
            }
            for (FacadeShape oShape : FacadeShape.getAllItemShapes()) {
                if (shape == oShape) {
                    continue;
                }
                int to = oShape.recipeMicroVoxelVolume;

                if (from > to) {
                    continue;
                }
                int ratio = to / from;
                if (ratio > 64) {
                    continue;
                }
                FullFacade newFacade = new FullFacade(facade.state, oShape);
                ItemStack input = SimplePipeItems.FACADE.createItemStack(newFacade);
                ItemStack output = SimplePipeItems.FACADE.createItemStack(facade);
                output.setCount(ratio);
                gen.accept(input, output);
            }
            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(list);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<DefaultStoneCuttingDisplay>> getUsageFor(EntryStack<?> entry) {
        if (entry.getValue() instanceof ItemStack itemStack) {
            if (!(itemStack.getItem() instanceof ItemFacade)) {
                return Optional.empty();
            }
            if (itemStack.getItem() == null || itemStack.isEmpty()) {
                return Optional.empty();
            }
            FullFacade facade = ItemFacade.getStates(itemStack.copy());
            if (!canCut(facade.state.state)) {
                return Optional.empty();
            }
            List<DefaultStoneCuttingDisplay> list = new ArrayList<>();
            BiConsumer<ItemStack, ItemStack> gen = (input, output) -> {
                Ingredient ing = IngredientHelper.fromStacks(input.copy());
                list.add(new DefaultStoneCuttingDisplay(new RecipeEntry<>(Identifier.of("buildcraftsilicon:facade_generated"), new StonecuttingRecipe("", ing, output.copy()))));
            };
            ItemStack blockStack = facade.state.requiredStack.copy();
            blockStack.setCount(1);

            FacadeShape shape = facade.shape;
            int from = shape.getRecipeMicroVoxelVolume();
            if (4096 / from <= 64) {
                ItemStack output = SimplePipeItems.FACADE.createItemStack(facade);
                output.setCount(4096 / from);
                gen.accept(blockStack, output);
            }
            for (FacadeShape oShape : FacadeShape.getAllItemShapes()) {
                if (shape == oShape) {
                    continue;
                }
                int to = oShape.recipeMicroVoxelVolume;
                if (from > to) {
                    continue;
                }
                int ratio = to / from;
                if (ratio > 64) {
                    continue;
                }
                FullFacade newFacade = new FullFacade(facade.state, oShape);
                ItemStack input = SimplePipeItems.FACADE.createItemStack(facade);
                ItemStack output = SimplePipeItems.FACADE.createItemStack(newFacade);
                input.setCount(ratio);
                output.setCount(1);
                gen.accept(input, output);
            }
            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(list);
            }
        }
        return Optional.empty();
    }

    private static boolean canCut(BlockState state) {
        // Stone pickaxe (not iron) so that there's actually a reason to upgrade to the laser cutter
        return !state.isToolRequired() || new ItemStack(Items.STONE_PICKAXE).isSuitableFor(state);
    }
}