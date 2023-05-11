package alexiil.mc.mod.pipes.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(GUI, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, 8, 6, 0x40_40_40, false);
        context.drawText(
            textRenderer, handler.player.getInventory().getDisplayName(), 8, backgroundHeight - 92, 0x40_40_40, false);
    }
}
