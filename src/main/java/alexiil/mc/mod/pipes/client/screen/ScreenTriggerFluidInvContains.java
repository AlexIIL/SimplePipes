package alexiil.mc.mod.pipes.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;

import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;
import alexiil.mc.mod.pipes.container.ContainerTriggerFluidContains;

public class ScreenTriggerFluidInvContains extends ContainerScreen<ContainerTriggerFluidContains> {

    public static final ContainerScreenFactory<ContainerTriggerFluidContains> FACTORY =
        ScreenTriggerFluidInvContains::new;

    private static final Identifier TRIGGER_GUI =
        new Identifier(SimplePipes.MODID, "textures/gui/trigger_fluid_inv.png");

    public ScreenTriggerFluidInvContains(ContainerTriggerFluidContains container) {
        super(container, container.player.inventory, SimplePipeBlocks.TRIGGER_FLUID_INV_CONTAINS.getTextComponent());
        height = 153;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawBackground();
        super.render(mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(TRIGGER_GUI);
        int int_3 = (this.screenWidth - this.width) / 2;
        int int_4 = (this.screenHeight - this.height) / 2;
        drawTexturedRect(int_3, int_4, 0, 0, this.width, this.height);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        fontRenderer.draw(title.getFormattedText(), 8.0F, 6.0F, 0x40_40_40);
        fontRenderer.draw(playerInventory.getDisplayName().getFormattedText(), 8.0F, height - 96 + 2, 0x40_40_40);
    }
}
