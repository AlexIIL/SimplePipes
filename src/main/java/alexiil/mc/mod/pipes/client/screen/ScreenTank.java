package alexiil.mc.mod.pipes.client.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import alexiil.mc.lib.attributes.fluid.volume.FluidUnit;
import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.container.ContainerTank;
import alexiil.mc.mod.pipes.items.SimplePipeItems;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

public class ScreenTank extends AbstractContainerScreen<ContainerTank> {

    public static final ContainerScreenFactory<ContainerTank> FACTORY = ScreenTank::new;

    private static final Identifier TANK_GUI = new Identifier(SimplePipes.MODID, "textures/gui/tank.png");

    public ScreenTank(ContainerTank container) {
        super(container, container.player.inventory, SimplePipeItems.TANK.getName());
        containerHeight = 176;
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
        bindTexture(TANK_GUI);
        int x = (this.width - this.containerWidth) / 2;
        int y = (this.height - this.containerHeight) / 2;
        blit(x, y, 0, 0, this.containerWidth, this.containerHeight);
        FluidStackInterp fluid = container.part.getFluidForRender(partialTicks);
        if (fluid != null && !fluid.fluid.isEmpty() && fluid.amount > 0.1) {
            double x0 = x + 80 + 0;
            double y0 = y + 23 + 48 - 48 * fluid.amount / container.part.fluidInv.tankCapacity;
            double x1 = x + 80 + 16;
            double y1 = y + 23 + 48;
            fluid.fluid.renderGuiRect(x0, y0, x1, y1);
        }
        bindTexture(TANK_GUI);
        blit(x + 80, y + 23, 176, 0, 16, 48);
    }

    private static void bindTexture(Identifier tex) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(tex);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        FluidStackInterp fluid = container.part.getFluidForRender(1);
        font.draw(title.asFormattedString(), 8.0F, 6.0F, 0x40_40_40);
        font.draw(playerInventory.getDisplayName().asFormattedString(), 8.0F, containerHeight - 96 + 2, 0x40_40_40);
        font.draw(fluid == null ? "null" : fluid.fluid.toString(), -12, -24, -1);
        font.draw(fluid == null ? "null" : fluid.fluid.getAmount_F().toString(), -12, -12, -1);
    }

    @Override
    protected void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
        int x = (this.width - this.containerWidth) / 2;
        int y = (this.height - this.containerHeight) / 2;
        int capacity = container.part.fluidInv.tankCapacity;
        if (mouseX >= x + 80 && mouseX <= x + 96 && mouseY >= y + 23 && mouseY <= y + 71) {
            FluidStackInterp fluid = container.part.getFluidForRender(1);
            if (fluid == null || fluid.fluid.isEmpty()) {
                List<String> str = new ArrayList<>();
                str.add(FluidUnit.BUCKET.localizeTank(0, capacity));
                renderTooltip(str, mouseX, mouseY);
                return;
            }
            List<Text> tooltip = fluid.fluid.getTooltipText(TooltipContext.Default.NORMAL);
            List<String> str = new ArrayList<>();
            for (Text text : tooltip) {
                str.add(text.asFormattedString());
            }
            str.add(fluid.fluid.fluidKey.unitSet.localizeTank(fluid.fluid.getAmount(), capacity));
            renderTooltip(str, mouseX, mouseY);
        }
    }
}
