package alexiil.mc.mod.pipes.mixin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.mixin.api.FindMatchingRecipesEvent;
import alexiil.mc.mod.pipes.mixin.api.RecipeMatchFinder;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Unique
    private boolean isHandlingAllMatches = false;

    @Inject(
        at = @At("HEAD"),
        method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/input/RecipeInput;" +
            "Lnet/minecraft/world/World;Lnet/minecraft/recipe/RecipeEntry;)Ljava/util/Optional;",
        cancellable = true)
    public <I extends RecipeInput, T extends Recipe<I>> void findFirst(RecipeType<T> type, I input, World world,
                                                                       RecipeEntry<T> recipe,
                                                                       CallbackInfoReturnable<Optional<RecipeEntry<T>>> cir) {

        List<RecipeEntry<T>> list = new ArrayList<>(1);
        invoke(new RecipeMatchFinder(type, input, world, val -> list.add((RecipeEntry<T>) val), true));
        if (!list.isEmpty()) {
            cir.setReturnValue(Optional.of(list.getFirst()));
        }
    }

    @Inject(
        at = @At("HEAD"),
        method = "getAllMatches",
        cancellable = true)
    public <I extends RecipeInput, T extends Recipe<I>> void getAllMatches(RecipeType<T> type, I input, World world,
                                                                         CallbackInfoReturnable<List<RecipeEntry<T>>> cir) {

        if (!isHandlingAllMatches) {
            try {
                isHandlingAllMatches = true;
                List<RecipeEntry<T>> list = new ArrayList<>();
                invoke(new RecipeMatchFinder(type, input, world, val -> list.add((RecipeEntry<T>) val), false));
                list.addAll(((RecipeManager) (Object) this).getAllMatches(type, input, world));
                cir.setReturnValue(list);
                return;
            } finally {
                isHandlingAllMatches = false;
            }
        }
    }

    private static void invoke(RecipeMatchFinder lastSeenMatchFinder) {

        FindMatchingRecipesEvent.EVENT.invoker().addRecipes(lastSeenMatchFinder);
    }
}
