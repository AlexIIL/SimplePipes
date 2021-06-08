package alexiil.mc.mod.pipes.part;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.items.ItemFacade;
import alexiil.mc.mod.pipes.items.SimplePipeItems;
import alexiil.mc.mod.pipes.mixin.api.FindMatchingRecipesEvent;
import alexiil.mc.mod.pipes.mixin.api.RecipeMatchFinder;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;
import alexiil.mc.mod.pipes.pipe.PipeSpBehaviour;
import alexiil.mc.mod.pipes.pipe.PipeSpBehaviourWood;
import alexiil.mc.mod.pipes.pipe.PipeSpDef;
import alexiil.mc.mod.pipes.pipe.PipeSpDef.PipeDefFluid;
import alexiil.mc.mod.pipes.pipe.PipeSpDef.PipeDefItem;
import alexiil.mc.mod.pipes.util.EnumCuboidCorner;
import alexiil.mc.mod.pipes.util.EnumCuboidEdge;
import alexiil.mc.mod.pipes.util.IngredientHelper;

import alexiil.mc.lib.attributes.item.ItemStackCollections;

import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.PartDefinition.IPartNbtReader;
import alexiil.mc.lib.multipart.api.PartDefinition.IPartNetLoader;

public final class SimplePipeParts {
    private SimplePipeParts() {}

    public static final PartDefinition FACADE;
    public static final PartDefinition TANK;

    public static final PipeSpDef.PipeDefItem WOODEN_PIPE_ITEMS;
    public static final PipeSpDef.PipeDefItem STONE_PIPE_ITEMS;
    public static final PipeSpDef.PipeDefItem CLAY_PIPE_ITEMS;
    public static final PipeSpDef.PipeDefItem IRON_PIPE_ITEMS;
    public static final PipeSpDef.PipeDefItem GOLD_PIPE_ITEMS;
    public static final PipeSpDef.PipeDefItem DIAMOND_PIPE_ITEMS;

    public static final PipeSpDef.PipeDefFluid WOODEN_PIPE_FLUIDS;
    public static final PipeSpDef.PipeDefFluid STONE_PIPE_FLUIDS;
    public static final PipeSpDef.PipeDefFluid CLAY_PIPE_FLUIDS;
    public static final PipeSpDef.PipeDefFluid IRON_PIPE_FLUIDS;
    public static final PipeSpDef.PipeDefFluid SPONGE_PIPE_FLUIDS;

    static {
        FACADE = def("facade", FacadePart::new, FacadePart::new);
        TANK = def("tank", PartTank::new, PartTank::new);

        WOODEN_PIPE_ITEMS = new PipeDefItem(id("wooden_pipe_items"), true, false, 2) {
            @Override
            public PipeSpBehaviour createBehaviour(PartSpPipe pipe) {
                return new PipeSpBehaviourWood(pipe);
            }
        };
        STONE_PIPE_ITEMS = new PipeDefItem(id("stone_pipe_items"), false, false, 1);
        CLAY_PIPE_ITEMS = new PipeDefItem(id("clay_pipe_items"), false, false, 1);
        IRON_PIPE_ITEMS = new PipeDefItem(id("iron_pipe_items"), false, false, 1);
        GOLD_PIPE_ITEMS = new PipeDefItem(id("gold_pipe_items"), false, false, 6);
        DIAMOND_PIPE_ITEMS = new PipeDefItem(id("diamond_pipe_items"), false, false, 1);

        WOODEN_PIPE_FLUIDS = new PipeDefFluid(id("wooden_pipe_fluids"), true) {
            @Override
            public PipeSpBehaviour createBehaviour(PartSpPipe pipe) {
                return new PipeSpBehaviourWood(pipe);
            }
        };
        STONE_PIPE_FLUIDS = new PipeDefFluid(id("stone_pipe_fluids"), false);
        CLAY_PIPE_FLUIDS = new PipeDefFluid(id("clay_pipe_fluids"), false);
        IRON_PIPE_FLUIDS = new PipeDefFluid(id("iron_pipe_fluids"), false);
        SPONGE_PIPE_FLUIDS = new PipeDefFluid(id("sponge_pipe_fluids"), false);
    }

    private static Identifier id(String path) {
        return SimplePipes.id(path);
    }

    private static PartDefinition def(String path, IPartNbtReader reader, IPartNetLoader loader) {
        return new PartDefinition(id(path), reader, loader);
    }

    public static void load() {
        FACADE.register();
        TANK.register();

        WOODEN_PIPE_ITEMS.register();
        STONE_PIPE_ITEMS.register();
        CLAY_PIPE_ITEMS.register();
        IRON_PIPE_ITEMS.register();
        GOLD_PIPE_ITEMS.register();
        DIAMOND_PIPE_ITEMS.register();

        WOODEN_PIPE_FLUIDS.register();
        STONE_PIPE_FLUIDS.register();
        CLAY_PIPE_FLUIDS.register();
        IRON_PIPE_FLUIDS.register();
        SPONGE_PIPE_FLUIDS.register();

        FindMatchingRecipesEvent.EVENT.register(SimplePipeParts::addFacadeRecipes);
        Registry.register(Registry.RECIPE_SERIALIZER, FacadeCraftingRecipe.ID, FacadeCraftingRecipe.INSTANCE);
    }

    private static void addFacadeRecipes(RecipeMatchFinder context) {
        if (context.recipeType == RecipeType.STONECUTTING) {

            FacadeStateManager facades = FacadeStateManager.getInstance();
            Set<ItemStack> seen = ItemStackCollections.set();

            for (int i = 0; i < context.inventory.size(); i++) {
                ItemStack stack = context.inventory.getStack(i);
                if (stack.isEmpty()) {
                    continue;
                }
                stack = stack.copy();
                stack.setCount(1);
                if (!seen.add(stack)) {
                    continue;
                }

                if (stack.getItem() instanceof ItemFacade) {
                    generateFacadeToFacadeCuttingRecipes(context, stack);
                    continue;
                }

                List<FacadeBlockStateInfo> states = FacadeStateManager.getStackFacades().get(stack);
                if (states == null) {
                    continue;
                }
                for (FacadeBlockStateInfo state : states) {
                    if (canCut(context, state.state)) {
                        generateBlockToFacadeCuttingRecipes(context.consumer, facades, stack, state);
                    }
                }
            }
        }
    }

    private static void generateBlockToFacadeCuttingRecipes(
        Consumer<Recipe<?>> recipeAdder, FacadeStateManager facades, ItemStack stack, FacadeBlockStateInfo state
    ) {
        Identifier id = new Identifier("buildcraftsilicon:facade_generated");
        Ingredient ingredient = createIngredient(stack);

        for (FacadeSize size : FacadeSize.values()) {
            for (boolean hollow : new boolean[] { false, true }) {
                FacadeShape.Sided shape = FacadeShape.Sided.get(size, Direction.WEST, hollow);
                ItemStack output = SimplePipeItems.FACADE.createItemStack(new FullFacade(state, shape));
                output.setCount(16 / size.microVoxelSize);
                recipeAdder.accept(new StonecuttingRecipe(id, "", ingredient, output));
            }
        }

        int[] stripAmounts = { 4, 16, 64 };
        for (int i = 0; i < FacadeSize.values().length; i++) {
            FacadeSize size = FacadeSize.values()[i];
            FacadeShape.Strip shape = FacadeShape.Strip.get(size, EnumCuboidEdge.Z_NN);
            ItemStack output = SimplePipeItems.FACADE.createItemStack(new FullFacade(state, shape));
            output.setCount(stripAmounts[i]);
            recipeAdder.accept(new StonecuttingRecipe(id, "", ingredient, output));
        }
        {
            FacadeShape.Corner shape = FacadeShape.Corner.get(FacadeSize.SLAB, EnumCuboidCorner.NNN);
            ItemStack output = SimplePipeItems.FACADE.createItemStack(new FullFacade(state, shape));
            output.setCount(8);
            recipeAdder.accept(new StonecuttingRecipe(id, "", ingredient, output));
        }
        {
            FacadeShape.Corner shape = FacadeShape.Corner.get(FacadeSize.THICK, EnumCuboidCorner.NNN);
            ItemStack output = SimplePipeItems.FACADE.createItemStack(new FullFacade(state, shape));
            output.setCount(64);
            recipeAdder.accept(new StonecuttingRecipe(id, "", ingredient, output));
        }
    }

    private static void generateFacadeToFacadeCuttingRecipes(RecipeMatchFinder context, ItemStack stack) {
        ItemFacade facadeItem = (ItemFacade) stack.getItem();
        FullFacade facade = ItemFacade.getStates(stack);
        if (facade == null) {
            return;
        }
        FacadeBlockStateInfo state = facade.state;
        if (!canCut(context, state.state)) {
            return;
        }

        Identifier id = new Identifier("buildcraftsilicon:facade_generated");
        Ingredient ingredient = createIngredient(stack);

        FacadeShape shape = facade.shape;
        int from = shape.getRecipeMicroVoxelVolume();
        for (FacadeShape oShape : FacadeShape.getAllItemShapes()) {
            if (shape == oShape) {
                continue;
            }
            int to = oShape.recipeMicroVoxelVolume;
            if (from < to) {
                continue;
            }
            int ratio = from / to;
            if (ratio > 64) {
                continue;
            }
            FullFacade newFacade = new FullFacade(state, oShape);
            ItemStack output = facadeItem.createItemStack(newFacade);
            output.setCount(ratio);
            context.consumer.accept(new StonecuttingRecipe(id, "", ingredient, output));
        }
    }

    private static boolean canCut(RecipeMatchFinder context, BlockState state) {
        // Stone pickaxe (not iron) so that there's actually a reason to upgrade to the laser cutter
        return !state.isToolRequired() || new ItemStack(Items.STONE_PICKAXE).isSuitableFor(state);
    }

    private static Ingredient createIngredient(ItemStack stack) {
        return IngredientHelper.fromStacks(stack);
    }
}
