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
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.mixin.api.FindMatchingRecipesEvent;
import alexiil.mc.mod.pipes.mixin.api.RecipeMatchFinder;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Unique
    private boolean isHandlingAllMatches = false;

    @Inject(
        at = @At("HEAD"),
        method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;"
            + "Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;",
        cancellable = true)
    public <C extends Inventory, R extends Recipe<C>> void findFirst(RecipeType<R> type, C inv, World world,
        CallbackInfoReturnable<Optional<RecipeEntry<R>>> ci) {

        List<RecipeEntry<R>> list = new ArrayList<>(1);
        invoke(new RecipeMatchFinder(type, inv, world, val -> list.add((RecipeEntry<R>) val), true));
        if (!list.isEmpty()) {
            ci.setReturnValue(Optional.of(list.get(0)));
        }
    }

    @Inject(
        at = @At("HEAD"),
        method = "getAllMatches(Lnet/minecraft/recipe/RecipeType;"
            + "Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;",
        cancellable = true)
    public <C extends Inventory, R extends Recipe<C>> void getAllMatches(RecipeType<R> type, C inv, World world,
        CallbackInfoReturnable<List<RecipeEntry<R>>> ci) {

        if (!isHandlingAllMatches) {
            try {
                isHandlingAllMatches = true;
                List<RecipeEntry<R>> list = new ArrayList<>();
                invoke(new RecipeMatchFinder(type, inv, world, val -> list.add((RecipeEntry<R>) val), false));
                list.addAll(((RecipeManager) (Object) this).getAllMatches(type, inv, world));
                ci.setReturnValue(list);
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
