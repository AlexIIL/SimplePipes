package alexiil.mc.mod.pipes.part;

import com.mojang.serialization.MapCodec;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.items.ItemFacade;
import alexiil.mc.mod.pipes.items.SimplePipeItems;

public enum FacadeCraftingRecipe implements Recipe<RecipeInput>, RecipeSerializer<FacadeCraftingRecipe> {
    INSTANCE;

    public static final Identifier ID = SimplePipes.id("facade_crafting");
    public static final MapCodec<FacadeCraftingRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final PacketCodec<RegistryByteBuf, FacadeCraftingRecipe> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public boolean matches(RecipeInput inv, World world) {
        return !craft(inv).isEmpty();
    }

    @Override
    public ItemStack craft(RecipeInput inventory, RegistryWrapper.WrapperLookup registryManager) {
        return craft(inventory);
    }

    public ItemStack craft(RecipeInput inv) {

        FacadeBlockStateInfo state = null;
        int microVoxelCount = 0;
        int seenFacades = 0;

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!(stack.getItem() instanceof ItemFacade)) {
                return ItemStack.EMPTY;
            }
            ItemFacade facadeItem = (ItemFacade) stack.getItem();
            FullFacade facade = ItemFacade.getStates(stack);
            if (facade == null) {
                return ItemStack.EMPTY;
            }
            FacadeBlockStateInfo state2 = facade.state;
            if (state != null && state != state2) {
                return ItemStack.EMPTY;
            }
            state = state2;
            microVoxelCount += facade.shape.getRecipeMicroVoxelVolume();
            seenFacades++;
        }

        if (state == null || seenFacades < 2) {
            return ItemStack.EMPTY;
        }

        for (FacadeShape shape : FacadeShape.getAllItemShapes()) {
            if (shape.recipeMicroVoxelVolume == microVoxelCount) {
                return SimplePipeItems.FACADE.createItemStack(new FullFacade(state, shape));
            }
        }

        if (microVoxelCount == 16 * 16 * 16) {
            return state.requiredStack.copy();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height > 1;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.STONECUTTING;
    }

    @Override
    public MapCodec<FacadeCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, FacadeCraftingRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
