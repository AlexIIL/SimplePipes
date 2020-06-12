package alexiil.mc.mod.pipes.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;
import alexiil.mc.mod.pipes.container.ContainerTriggerInvSpace;

public class ScreenTriggerItemInvSpace extends HandledScreen<ContainerTriggerInvSpace> {

    public static final ContainerScreenFactory<ContainerTriggerInvSpace> FACTORY = ScreenTriggerItemInvSpace::new;

    private static final Identifier TRIGGER_GUI
        = new Identifier(SimplePipes.MODID, "textures/gui/trigger_item_inv.png");

    public ScreenTriggerItemInvSpace(ContainerTriggerInvSpace container) {
        super(container, container.player.inventory, SimplePipeBlocks.TRIGGER_ITEM_INV_SPACE.getName());
        backgroundHeight = 153;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        MinecraftClient.getInstance().getTextureManager().bindTexture(TRIGGER_GUI);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        textRenderer.draw(matrices, title, 8.0F, 6.0F, 0x40_40_40);
        textRenderer.draw(matrices, playerInventory.getDisplayName(), 8.0F, backgroundHeight - 96 + 2, 0x40_40_40);
    }
}
