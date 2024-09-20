package dev.luan.server.space.command;

import dev.luan.server.MultiServer;
import dev.luan.server.MultiServerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

public final class MultiServerLobbyCommand extends Command {

    public MultiServerLobbyCommand() {
        super("lobby", "hub");

        setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof Player player)) {
                return;
            }

            var lobbyInstance = MultiServer.instance().spaceFactory().findLobby(player);
            if (lobbyInstance == null) {
                return;
            }

            if(player.getInstance() == lobbyInstance) {
                player.sendMessage(MultiServerData.text("§cYou are already in the lobby!"));
                return;
            }

            player.setInstance(lobbyInstance);
            player.sendMessage(MultiServerData.text("§7You have been send to the lobby!"));
        });

        MinecraftServer.getCommandManager().register(this);
    }
}