package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class SimplePipeContainerFactory<T> implements ExtendedScreenHandlerFactory<T> {
    private final OpeningDataGetter<T> getter;
    private final Text displayName;
    private final ContainerCreator creator;

    public SimplePipeContainerFactory(Text displayName, ContainerCreator creator, OpeningDataGetter<T> getter) {
        this.getter = getter;
        this.displayName = displayName;
        this.creator = creator;
    }

    @Override
    public T getScreenOpeningData(ServerPlayerEntity player) {
        return getter.getScreenOpeningData(player);
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
    public interface OpeningDataGetter<T> {
        T getScreenOpeningData(ServerPlayerEntity player);
    }

    @FunctionalInterface
    public interface ContainerCreator {
        @Nullable
        ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player);
    }
}
