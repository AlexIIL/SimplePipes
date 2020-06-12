package alexiil.mc.mod.pipes.client.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidTooltipContext;
import alexiil.mc.lib.attributes.fluid.volume.FluidUnit;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.container.ContainerTank;
import alexiil.mc.mod.pipes.items.SimplePipeItems;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

public class ScreenTank extends HandledScreen<ContainerTank> {

    public static final ContainerScreenFactory<ContainerTank> FACTORY = ScreenTank::new;

    private static final Identifier TANK_GUI = new Identifier(SimplePipes.MODID, "textures/gui/tank.png");

    public ScreenTank(ContainerTank container) {
        super(container, container.player.inventory, SimplePipeItems.TANK.getName());
        backgroundHeight = 176;
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
        bindTexture(TANK_GUI);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        FluidStackInterp fluid = handler.part.getFluidForRender(partialTicks);
        if (fluid != null && !fluid.fluid.isEmpty() && fluid.amount > 0.1) {
            double x0 = x + 80 + 0;
            double y0 = y + 23 + 48 - 48 * fluid.amount / handler.part.fluidInv.tankCapacity;
            double x1 = x + 80 + 16;
            double y1 = y + 23 + 48;
            fluid.fluid.renderGuiRect(x0, y0, x1, y1);
        }
        bindTexture(TANK_GUI);
        drawTexture(matrices, x + 80, y + 23, 176, 0, 16, 48);
    }

    private static void bindTexture(Identifier tex) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(tex);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        textRenderer.draw(matrices, title, 8.0F, 6.0F, 0x40_40_40);
        textRenderer.draw(matrices, playerInventory.getDisplayName(), 8.0F, backgroundHeight - 96 + 2, 0x40_40_40);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(matrices, mouseX, mouseY);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        FluidAmount capacity = handler.part.fluidInv.tankCapacity_F;
        if (mouseX >= x + 80 && mouseX <= x + 96 && mouseY >= y + 23 && mouseY <= y + 71) {
            FluidVolume fluid = handler.part.smoothedTank.getFluidForRender();
            if (fluid == null || fluid.isEmpty()) {
                List<Text> str = new ArrayList<>();
                str.add(FluidUnit.BUCKET.getEmptyTank(capacity));
                renderTooltip(matrices, str, mouseX, mouseY);
                return;
            }
            renderTooltip(matrices, fluid.getFullTooltip(capacity, FluidTooltipContext.USE_CONFIG), mouseX, mouseY);
        }
    }
}
