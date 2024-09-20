package dev.luan.server.event;

import dev.luan.server.space.MultiServerSpace;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.trait.InstanceEvent;

import java.util.function.Consumer;

public final class MultiServerEventFactory {

    public <T extends InstanceEvent> void listen(MultiServerSpace space, Class<T> eventType, Consumer<T> listener) {
        MinecraftServer.getGlobalEventHandler().addListener(eventType, t -> {
            if(space.instances().stream().anyMatch(it -> it.equals(t.getInstance()))) {
                listener.accept(t);
            }
        });
    }
}
