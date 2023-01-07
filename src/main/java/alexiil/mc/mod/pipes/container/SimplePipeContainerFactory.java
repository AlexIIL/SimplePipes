package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class SimplePipeContainerFactory implements ExtendedScreenHandlerFactory {
    private final OpeningDataWriter writer;
    private final Text displayName;
    private final ContainerCreator creator;

    public SimplePipeContainerFactory(Text displayName, ContainerCreator creator, OpeningDataWriter writer) {
        this.writer = writer;
        this.displayName = displayName;
        this.creator = creator;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        writer.writeScreenOpeningData(player, buf);
    }

    @Override
    public Text getDisplayName() {
        return displayName;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return creator.createMenu(syncId, inv, player);
    }

    @FunctionalInterface
    public interface OpeningDataWriter {
        void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf);
    }

    @FunctionalInterface
    public interface ContainerCreator {
        @Nullable
        ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player);
    }
}
