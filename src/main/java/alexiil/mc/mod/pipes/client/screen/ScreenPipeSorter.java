package alexiil.mc.mod.pipes.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.container.ContainerPipeSorter;

public class ScreenPipeSorter extends HandledScreen<ContainerPipeSorter> {

    public static final HandledScreens.Provider<ContainerPipeSorter, ScreenPipeSorter> FACTORY = ScreenPipeSorter::new;

    private static final Identifier GUI = new Identifier(SimplePipes.MODID, "textures/gui/filter.png");

    public ScreenPipeSorter(ContainerPipeSorter container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        backgroundHeight = 222;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        textRenderer.draw(matrices, title, 8.0F, 6.0F, 0x40_40_40);
        textRenderer
            .draw(matrices, handler.player.getInventory().getDisplayName(), 8.0F, backgroundHeight - 92, 0x40_40_40);
    }
}
