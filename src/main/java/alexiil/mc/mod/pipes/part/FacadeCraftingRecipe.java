package alexiil.mc.mod.pipes.part;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.items.ItemFacade;
import alexiil.mc.mod.pipes.items.SimplePipeItems;

public enum FacadeCraftingRecipe implements CraftingRecipe, RecipeSerializer<FacadeCraftingRecipe> {
    INSTANCE;

    public static final Identifier ID = SimplePipes.id("facade_crafting");

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return !craft(inv).isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        return craft(inventory);
    }

    public ItemStack craft(CraftingInventory inv) {

        FacadeBlockStateInfo state = null;
        int microVoxelCount = 0;
        int seenFacades = 0;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
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
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this;
    }

    @Override
    public FacadeCraftingRecipe read(Identifier id, JsonObject json) {
        if (id.equals(ID)) {
            return INSTANCE;
        }
        throw new JsonSyntaxException("Invalid ID '" + id + "': it must be " + ID + "!");
    }

    @Override
    public FacadeCraftingRecipe read(Identifier id, PacketByteBuf var2) {
        return INSTANCE;
    }

    @Override
    public void write(PacketByteBuf buffer, FacadeCraftingRecipe recipe) {
        // NO-OP
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return CraftingRecipeCategory.BUILDING;
    }
}
