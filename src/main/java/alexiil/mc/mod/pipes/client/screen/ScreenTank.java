package alexiil.mc.mod.pipes.client.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.container.ContainerTank;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidTooltipContext;
import alexiil.mc.lib.attributes.fluid.volume.FluidUnit;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public class ScreenTank extends HandledScreen<ContainerTank> {

    public static final HandledScreens.Provider<ContainerTank, ScreenTank> FACTORY = ScreenTank::new;

    private static final Identifier TANK_GUI = Identifier.of(SimplePipes.MODID, "textures/gui/tank.png");

    public ScreenTank(ContainerTank container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        backgroundHeight = 176;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        renderBackground(context, mouseX, mouseY, partialTicks);
        super.render(context, mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TANK_GUI, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        FluidStackInterp fluid = handler.part.getFluidForRender(partialTicks);
        if (fluid != null && !fluid.fluid.isEmpty() && fluid.amount > 0.1) {
            double x0 = x + 80 + 0;
            double y0 = y + 23 + 48 - 48 * fluid.amount / handler.part.fluidInv.tankCapacity_F.asInexactDouble();
            double x1 = x + 80 + 16;
            double y1 = y + 23 + 48;
            fluid.fluid.renderGuiRect(x0, y0, x1, y1);
        }
        context.drawTexture(TANK_GUI, x + 80, y + 23, 176, 0, 16, 48);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, 8, 6, 0x40_40_40, false);
        context.drawText(
            textRenderer, handler.player.getInventory().getDisplayName(), 8, backgroundHeight - 96 + 2, 0x40_40_40,
            false
        );
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(context, mouseX, mouseY);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        FluidAmount capacity = handler.part.fluidInv.tankCapacity_F;
        if (mouseX >= x + 80 && mouseX <= x + 96 && mouseY >= y + 23 && mouseY <= y + 71) {
            FluidVolume fluid = handler.part.smoothedTank.getFluidForRender();
            if (fluid == null || fluid.isEmpty()) {
                List<Text> str = new ArrayList<>();
                str.add(FluidUnit.BUCKET.getEmptyTank(capacity));
                context.drawTooltip(textRenderer, str, mouseX, mouseY);
                return;
            }
            context.drawTooltip(
                textRenderer, fluid.getFullTooltip(capacity, FluidTooltipContext.USE_CONFIG), mouseX, mouseY);
        }
    }
}
