package alexiil.mc.mod.pipes.mixin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.mixin.api.FindMatchingRecipesEvent;
import alexiil.mc.mod.pipes.mixin.api.RecipeMatchFinder;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Unique
    private RecipeMatchFinder<?, ?> lastSeenMatchFinder;

    @Redirect(
        at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;findFirst()Ljava/util/Optional;"),
        method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;"
            + "Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;")
    public <C extends Inventory, T extends Recipe<C>> Optional<T> redirectFindFirst(Stream<T> stream) {
        Optional<T> first = stream.findFirst();
        if (first.isPresent()) {
            return first;
        }
        List<T> list = new ArrayList<>(1);
        invoke(lastSeenMatchFinder, (Consumer<T>) list::add);

        lastSeenMatchFinder = null;
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"),
        method = "getAllMatches(Lnet/minecraft/recipe/RecipeType;"
            + "Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;")
    public <C extends Inventory, T extends Recipe<C>> Object redirectCollect(Stream<T> stream, Collector<T, ?, List<
        T>> collector) {
        List<T> collected = stream.collect(collector);
        final ArrayList<T> modList;
        if (collected instanceof ArrayList) {
            modList = (ArrayList<T>) collected;
        } else {
            modList = new ArrayList<>(collected);
        }

        invoke(lastSeenMatchFinder, (Consumer<T>) modList::add);

        lastSeenMatchFinder = null;
        return modList;
    }

    private static <C extends Inventory, R extends Recipe<C>> void invoke(RecipeMatchFinder<C, R> lastSeenMatchFinder,
        Consumer<?> consumer) {
        FindMatchingRecipesEvent.EVENT.invoker().addRecipes(lastSeenMatchFinder, (Consumer<Recipe<?>>) consumer);
    }

    @Inject(
        at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;findFirst()Ljava/util/Optional;"),
        method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;"
            + "Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;")
    public <C extends Inventory, T extends Recipe<C>> void getFirstMatch(RecipeType<T> type, C inv, World world,
        CallbackInfoReturnable<List<T>> ci) {
        lastSeenMatchFinder = new RecipeMatchFinder<>(type, inv, world, true);
    }

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"),
        method = "getAllMatches(Lnet/minecraft/recipe/RecipeType;"
            + "Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;")
    public <C extends Inventory, T extends Recipe<C>> void getAllMatches(RecipeType<T> type, C inv, World world,
        CallbackInfoReturnable<List<T>> ci) {
        lastSeenMatchFinder = new RecipeMatchFinder<>(type, inv, world, false);
    }
}
