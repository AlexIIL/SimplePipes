package alexiil.mc.mod.pipes.util;

import javax.annotation.Nullable;
import alexiil.mc.mod.pipes.util.RenderUtil.TessellatorQueue;
import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.Tessellator;
import net.minecraft.item.Item;

public final class RenderUtil {
    private RenderUtil() {}

    private static final ThreadLocal<TessellatorQueue> threadLocalTessellators;

    static {
        threadLocalTessellators = ThreadLocal.withInitial(TessellatorQueue::new);
    }

    public static void registerBlockColour(@Nullable Block block, BlockColorProvider colour) {
        if (block != null) {
            ColorProviderRegistry.BLOCK.register(colour, block);
        }
    }

    public static void registerItemColour(@Nullable Item item, ItemColorProvider colour) {
        if (item != null) {
            ColorProviderRegistry.ITEM.register(colour, item);
        }
    }

    public static void registerItemColour(@Nullable Item item) {
        registerItemColour(item, (stack, c) -> c == 0 ? -1 : c);
    }

    /** Takes _RGB (alpha is set to 1) */
    public static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        GlStateManager.color4f(red, green, blue, 1.0f);
    }

    /** Takes ARGB */
    public static void setGLColorFromIntPlusAlpha(int color) {
        float alpha = (color >>> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        GlStateManager.color4f(red, green, blue, alpha);
    }

    public static int swapARGBforABGR(int argb) {
        int a = (argb >>> 24) & 255;
        int r = (argb >> 16) & 255;
        int g = (argb >> 8) & 255;
        int b = (argb >> 0) & 255;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    public static boolean isRenderingTranslucent() {
        return false;
    }

    /** @return true if this thread is the main minecraft thread, used for all client side game logic and (by default)
     *         tile entity rendering. */
    public static boolean isMainRenderThread() {
        return MinecraftClient.getInstance().isOnThread();
    }

    /** @return The first unused {@link Tessellator} for the current thread. */
    public static AutoTessellator getThreadLocalUnusedTessellator() {
        return threadLocalTessellators.get().nextFreeTessellator();
    }

    private static Tessellator newTessellator() {
        // The same as what minecraft expands a tessellator by
        return new Tessellator(0x200_000);
    }

    static class TessellatorQueue {
        // Max size of 20: if we go over this then something has gone very wrong
        // In theory this shouldn't even go above about 3.
        private static final int BUFFER_COUNT = 20;

        final Tessellator[] tessellators = new Tessellator[BUFFER_COUNT];
        final boolean[] tessellatorInUse = new boolean[BUFFER_COUNT];

        AutoTessellator nextFreeTessellator() {
            for (int i = 0; i < tessellators.length; i++) {
                if (tessellatorInUse[i]) {
                    continue;
                }
                Tessellator tess = tessellators[i];
                if (tess == null) {
                    tess = newTessellator();
                    tessellators[i] = tess;
                }
                return new AutoTessellator(this, i);
            }
            /* Assume something has gone wrong as it seems quite odd to have this many buffers rendering at the same
             * time. */
            throw new Error("Too many tessellators! Has a caller not finished with one of them?");
        }
    }

    public static final class AutoTessellator implements AutoCloseable {
        private final TessellatorQueue queue;
        private final int index;
        public final Tessellator tessellator;

        public AutoTessellator(TessellatorQueue queue, int index) {
            this.queue = queue;
            this.index = index;
            this.tessellator = queue.tessellators[index];
            queue.tessellatorInUse[index] = true;
        }

        @Override
        public void close() {
            queue.tessellatorInUse[index] = false;
        }
    }
}
