package alexiil.mc.mod.pipes.compat.rei;

import net.minecraft.item.ItemStack;

import alexiil.mc.mod.pipes.items.SimplePipeItems;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

public class REIPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerDisplayGenerator(BuiltinPlugin.STONE_CUTTING, new FacadeDisplayGenerator());
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntryIf(
            stack -> stack.getValue() instanceof ItemStack s && s.getItem() == SimplePipeItems.FACADE
        );
    }
}
