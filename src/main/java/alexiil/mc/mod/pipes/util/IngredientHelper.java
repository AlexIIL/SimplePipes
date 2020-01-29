package alexiil.mc.mod.pipes.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

public final class IngredientHelper {
    private IngredientHelper() {}

    private static final Constructor<?> INGREIDIENT_CTOR;
    private static final Constructor<?> INGREIDIENT_STACK_CTOR;

    static {
        try {
            // Mixin doesn't permit grabbing constructors, so we have to do it manually
            Ingredient ing = Ingredient.ofItems(Items.STONE);
            Field entryField = null;
            for (Field f : ing.getClass().getDeclaredFields()) {
                Class<?> ft = f.getType();
                if (
                    ft.isArray() && ft.getComponentType().isInterface()
                    && ft.getComponentType().getEnclosingClass() == ing.getClass()
                ) {
                    if (entryField != null) {
                        throw new Error(
                            "Multiple fields for Ingredient that are both arrays of interfaces and contained in the Ingredient class!\n"
                                + entryField + " and " + f
                        );
                    }
                    entryField = f;
                }
            }
            if (entryField == null) {
                throw new Error("Didn't find the correct field in Ingredient!");
            }
            entryField.setAccessible(true);
            Object[] array = (Object[]) entryField.get(ing);
            INGREIDIENT_CTOR = ing.getClass().getDeclaredConstructor(Stream.class);
            INGREIDIENT_CTOR.setAccessible(true);

            INGREIDIENT_STACK_CTOR = array[0].getClass().getDeclaredConstructor(ItemStack.class);
            INGREIDIENT_STACK_CTOR.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }

    public static Object createIngredientEntry(ItemStack stack) {
        try {
            return INGREIDIENT_STACK_CTOR.newInstance(stack);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }

    /** Universal version (both client+server environments) of {@link Ingredient#ofStacks(ItemStack...)} */
    public static Ingredient fromStacks(ItemStack... stacks) {
        try {
            return (Ingredient) INGREIDIENT_CTOR
                .newInstance(Arrays.stream(stacks).map(IngredientHelper::createIngredientEntry));
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }
}
