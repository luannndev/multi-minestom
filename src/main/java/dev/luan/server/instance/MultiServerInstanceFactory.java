package dev.luan.server.instance;

import dev.luan.server.MultiServer;
import dev.luan.server.space.MultiServerSpace;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.effects.Effects;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.VanillaLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;

@Slf4j
public final class MultiServerInstanceFactory {

    public MultiServerInstanceFactory() {
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var lobbyInstance = MultiServer.instance().spaceFactory().findLobby(event.getPlayer());
            if(lobbyInstance != null) {
                event.setSpawningInstance(lobbyInstance);
            }
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerChatEvent.class, event -> {
            event.setCancelled(true);

            var space = MultiServer.instance().spaceFactory().spaces()
                    .stream()
                    .filter(it -> it.instances().stream().anyMatch(instance -> instance.getPlayers().contains(event.getPlayer())))
                    .findFirst()
                    .orElse(null);

            if (space == null) {
                event.getPlayer().sendMessage(Component.text("§8[§cmultiserver§8] §cYou are not connected to any space!"));
                return;
            }

            space.instances().forEach(instance -> instance.getPlayers().forEach(player -> player.sendMessage(Component.text("§8[§7" + event.getPlayer().getUsername() + "§8] §7" + event.getMessage()))));
        });

        MinecraftServer.getGlobalEventHandler().addListener(AddEntityToInstanceEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                player.addEffect(new Potion(PotionEffect.BLINDNESS, Byte.MAX_VALUE, 20));

                MultiServer.instance().spaceFactory().spaces().forEach(space -> {
                    if(space.instances().stream().anyMatch(it -> it.equals(event.getInstance()))) {
                        space.instances().forEach(instance -> instance.getPlayers().forEach(it -> {
                            if(it == player) {
                                return;
                            }
                            it.sendPacket(playerPacket(player));
                            player.sendPacket(playerPacket(it));
                        }));
                    } else {
                        space.instances().forEach(instance -> instance.getPlayers().forEach(it -> {
                            if(it == player) {
                                return;
                            }
                            it.sendPacket(new PlayerInfoRemovePacket(player.getUuid()));
                            player.sendPacket(new PlayerInfoRemovePacket(it.getUuid()));
                        }));
                    }
                });
            }
        });
    }

    private PlayerInfoUpdatePacket playerPacket(Player player) {
        var infoEntry = new PlayerInfoUpdatePacket.Entry(player.getUuid(), player.getUsername(), List.of(
                new PlayerInfoUpdatePacket.Property("textures", player.getSkin().textures(), player.getSkin().signature())
        ), true, 1, player.getGameMode(), null, null);
        return new PlayerInfoUpdatePacket(EnumSet.of(PlayerInfoUpdatePacket.Action.ADD_PLAYER, PlayerInfoUpdatePacket.Action.UPDATE_LISTED), List.of(infoEntry));
    }

    public InstanceContainer loadExisting(MultiServerSpace space, Path path) {
        var container = create(space);
        container.setChunkLoader(new VanillaLoader(Path.of("tasks").resolve(space.template().folderName()).resolve(path)));

        return container;
    }

    public void register(MultiServerSpace space, InstanceContainer container) {
        MinecraftServer.getInstanceManager().registerInstance(container);
        container.setChunkSupplier(LightingChunk::new);

        space.instances().add(container);
    }

    public InstanceContainer create(MultiServerSpace space) {
        var container = MinecraftServer.getInstanceManager().createInstanceContainer();
        register(space, container);
        return container;
    }
}