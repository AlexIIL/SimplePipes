package alexiil.mc.mod.pipes.mixin.api;

import java.util.function.Consumer;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.world.World;

public final class RecipeMatchFinder {
    public final RecipeType<?> recipeType;
    public final RecipeInput inventory;
    public final World world;
    public final Consumer<RecipeEntry<Recipe<?>>> consumer;

    /** If true then the {@link #consumer} will only use the first recipe given to it. */
    public final boolean single;

    public RecipeMatchFinder(RecipeType<?> recipeType, RecipeInput inventory, World world, Consumer<RecipeEntry<Recipe<?>>> consumer,
        boolean single) {

        this.recipeType = recipeType;
        this.inventory = inventory;
        this.world = world;
        this.consumer = consumer;
        this.single = single;
    }
}
