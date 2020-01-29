package alexiil.mc.mod.pipes.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;
import alexiil.mc.mod.pipes.container.ContainerPipeSorter;

public class ScreenPipeSorter extends ContainerScreen<ContainerPipeSorter> {

    public static final ContainerScreenFactory<ContainerPipeSorter> FACTORY = ScreenPipeSorter::new;

    private static final Identifier GUI = new Identifier(SimplePipes.MODID, "textures/gui/filter.png");

    public ScreenPipeSorter(ContainerPipeSorter container) {
        super(container, container.player.inventory, SimplePipeBlocks.DIAMOND_PIPE_ITEMS.getName());
        containerHeight = 222;
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
        MinecraftClient.getInstance().getTextureManager().bindTexture(GUI);
        int x = (this.width - this.containerWidth) / 2;
        int y = (this.height - this.containerHeight) / 2;
        blit(x, y, 0, 0, this.containerWidth, this.containerHeight);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        font.draw(title.asFormattedString(), 8.0F, 6.0F, 0x40_40_40);
        font.draw(playerInventory.getDisplayName().asFormattedString(), 8.0F, containerHeight - 92, 0x40_40_40);
    }
}
