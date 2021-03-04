package alexiil.mc.mod.pipes.compat.rei;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.items.ItemFacade;
import alexiil.mc.mod.pipes.items.SimplePipeItems;
import alexiil.mc.mod.pipes.part.FacadeBlockStateInfo;
import alexiil.mc.mod.pipes.part.FacadeShape;
import alexiil.mc.mod.pipes.part.FacadeStateManager;
import alexiil.mc.mod.pipes.part.FullFacade;
import alexiil.mc.mod.pipes.part.SimplePipeParts;
import alexiil.mc.mod.pipes.util.IngredientHelper;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.LiveRecipeGenerator;
import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.plugin.stonecutting.DefaultStoneCuttingDisplay;

public class FacadeRecipeGenerator implements LiveRecipeGenerator<DefaultStoneCuttingDisplay> {

    private static final Identifier RECIPE_ID = new Identifier("buildcraftsilicon:facade_generated");

    @Override
    public Identifier getCategoryIdentifier() {
        return DefaultPlugin.STONE_CUTTING;
    }

    @Override
    public Optional<List<DefaultStoneCuttingDisplay>> getRecipeFor(EntryStack entry) {
        Item item = entry.getItem();
        if (!(item instanceof ItemFacade)) {
            return Optional.empty();
        }

        FullFacade facade = ItemFacade.getStates(entry.getItemStack());
        if (!canCut(facade.state.state)) {
            return Optional.empty();
        }

        List<DefaultStoneCuttingDisplay> list = new ArrayList<>();
        BiConsumer<ItemStack, ItemStack> gen = (input, output) -> {
            Ingredient ing = IngredientHelper.fromStacks(input.copy());
            list.add(new DefaultStoneCuttingDisplay(new StonecuttingRecipe(RECIPE_ID, "", ing, output.copy())));
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

    private static boolean canCut(BlockState state) {
        // Stone pickaxe (not iron) so that there's actually a reason to upgrade to the laser cutter
        return !state.isToolRequired() || new ItemStack(Items.STONE_PICKAXE).isEffectiveOn(state);
    }

    @Override
    public Optional<List<DefaultStoneCuttingDisplay>> getUsageFor(EntryStack entry) {
        Item item = entry.getItem();
        if (item == null) {
            return Optional.empty();
        }

        List<DefaultStoneCuttingDisplay> list = new ArrayList<>();
        Consumer<Recipe<?>> consumer = recipe -> {
            list.add(new DefaultStoneCuttingDisplay((StonecuttingRecipe) recipe));
        };

        ItemStack stack = entry.getItemStack();
        stack = stack.copy();
        stack.setCount(1);

        if (item instanceof ItemFacade) {
            SimplePipeParts.generateFacadeToFacadeCuttingRecipes(consumer, stack);
        } else {
            List<FacadeBlockStateInfo> facadeBlocks = FacadeStateManager.getStackFacades().get(stack);
            if (facadeBlocks == null || facadeBlocks.isEmpty()) {
                return Optional.empty();
            }

            for (FacadeBlockStateInfo info : facadeBlocks) {
                if (!canCut(info.state)) {
                    continue;
                }
                SimplePipeParts
                    .generateBlockToFacadeCuttingRecipes(consumer, FacadeStateManager.getInstance(), stack, info);
            }
        }

        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list);
        }
    }
}
