package alexiil.mc.mod.pipes.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;
import alexiil.mc.mod.pipes.container.ContainerTriggerFluidSpace;

public class ScreenTriggerFluidInvSpace extends ContainerScreen<ContainerTriggerFluidSpace> {

    public static final ContainerScreenFactory<ContainerTriggerFluidSpace> FACTORY = ScreenTriggerFluidInvSpace::new;

    private static final Identifier TRIGGER_GUI
        = new Identifier(SimplePipes.MODID, "textures/gui/trigger_fluid_inv.png");

    public ScreenTriggerFluidInvSpace(ContainerTriggerFluidSpace container) {
        super(container, container.player.inventory, SimplePipeBlocks.TRIGGER_FLUID_INV_SPACE.getName());
        containerHeight = 153;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        MinecraftClient.getInstance().getTextureManager().bindTexture(TRIGGER_GUI);
        int x = (this.width - this.containerWidth) / 2;
        int y = (this.height - this.containerHeight) / 2;
        blit(x, y, 0, 0, this.containerWidth, this.containerHeight);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        font.draw(title.asFormattedString(), 8.0F, 6.0F, 0x40_40_40);
        font.draw(playerInventory.getDisplayName().asFormattedString(), 8.0F, containerHeight - 96 + 2, 0x40_40_40);
    }
}
