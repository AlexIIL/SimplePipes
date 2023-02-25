package alexiil.mc.mod.pipes.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

import alexiil.mc.mod.pipes.items.SimplePipeItems;

public class EMIPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.removeEmiStacks(emiStack -> emiStack.getItemStack().getItem() == SimplePipeItems.FACADE);
    }
}
