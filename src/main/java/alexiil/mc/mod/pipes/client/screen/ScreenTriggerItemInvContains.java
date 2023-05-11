package alexiil.mc.mod.pipes.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.container.ContainerTriggerInvContains;

public class ScreenTriggerItemInvContains extends HandledScreen<ContainerTriggerInvContains> {

    public static final HandledScreens.Provider<ContainerTriggerInvContains, ScreenTriggerItemInvContains> FACTORY
        = ScreenTriggerItemInvContains::new;

    private static final Identifier TRIGGER_GUI
        = new Identifier(SimplePipes.MODID, "textures/gui/trigger_item_inv.png");

    public ScreenTriggerItemInvContains(ContainerTriggerInvContains container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        backgroundHeight = 153;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TRIGGER_GUI, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, 8, 6, 0x40_40_40, false);
        context.drawText(
            textRenderer, handler.player.getInventory().getDisplayName(), 8, backgroundHeight - 96 + 2, 0x40_40_40,
            false
        );
    }
}
