package alexiil.mc.mod.pipes.mixin.api;

import java.util.function.Consumer;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

public final class RecipeMatchFinder<I extends Inventory, R extends Recipe<I>> {
    public final RecipeType<R> recipeType;
    public final I inventory;
    public final World world;

    /** If true then the {@link FindMatchingRecipesEvent#addRecipes(RecipeMatchFinder, Consumer)} consumer will only use
     * the first recipe given to it. */
    public final boolean single;

    public RecipeMatchFinder(RecipeType<R> recipeType, I inventory, World world, boolean single) {
        this.recipeType = recipeType;
        this.inventory = inventory;
        this.world = world;
        this.single = single;
    }
}
