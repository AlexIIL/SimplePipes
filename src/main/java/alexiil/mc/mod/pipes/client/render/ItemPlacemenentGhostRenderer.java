package alexiil.mc.mod.pipes.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;

import alexiil.mc.mod.pipes.items.GhostPlacement;
import alexiil.mc.mod.pipes.items.IItemPlacmentGhost;

public final class ItemPlacemenentGhostRenderer {
    private ItemPlacemenentGhostRenderer() {}

    private static GhostPlacement currentPlacementGhost;

    public static void render(PlayerEntity player, float partialTicks) {

        if (currentPlacementGhost != null && currentPlacementGhost.isLockedOpen()) {
            currentPlacementGhost.render(player, partialTicks);
            return;
        }

        if (render(player, partialTicks, Hand.MAIN_HAND, player.getMainHandStack())) {
            return;
        }
        render(player, partialTicks, Hand.OFF_HAND, player.getOffHandStack());
    }

    public static void clientTick() {
        if (currentPlacementGhost != null) {
            currentPlacementGhost.tick();
        }
    }

    private static boolean render(PlayerEntity player, float partialTicks, Hand hand, ItemStack stack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult) || hit.getType() != Type.BLOCK) {
            setCurrentGhost(null);
            return true;
        }
        BlockHitResult blockHit = (BlockHitResult) hit;

        Item item = stack.getItem();
        if (item instanceof IItemPlacmentGhost) {
            IItemPlacmentGhost ghostItem = (IItemPlacmentGhost) item;
            ItemUsageContext ctx = new ItemUsageContext(player, hand, blockHit);

            if (currentPlacementGhost != null) {
                GhostPlacement ghost = currentPlacementGhost.preRender(ctx);
                if (ghost == null) {
                    setCurrentGhost(null);
                } else if (ghost != currentPlacementGhost) {
                    setCurrentGhost(ghost);
                }
            }

            if (currentPlacementGhost == null) {
                GhostPlacement ghost = ghostItem.createGhostPlacement(ctx);
                if (ghost != null) {
                    ghost = ghost.preRender(ctx);
                }
                setCurrentGhost(ghost);
            }
            if (currentPlacementGhost != null) {
                currentPlacementGhost.render(player, partialTicks);
                return true;
            }
        }
        return false;
    }

    private static void setCurrentGhost(GhostPlacement ghost) {
        if (currentPlacementGhost != null) {
            currentPlacementGhost.delete();
        }
        currentPlacementGhost = ghost;
    }
}
