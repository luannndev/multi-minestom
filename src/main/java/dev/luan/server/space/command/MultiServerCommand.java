package dev.luan.server.space.command;

import ch.qos.logback.core.util.SystemInfo;
import com.sun.management.OperatingSystemMXBean;
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
import net.minestom.server.utils.time.Tick;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

public final class MultiServerCommand extends Command {

    public MultiServerCommand() {
        super("multiserver");

        var typeArgument = ArgumentType.Enum("type", MultiServerCommandType.class);
        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                var type = context.get(typeArgument);
                if (type.equals(MultiServerCommandType.START)) {
                    sender.sendMessage(MultiServerData.text("§cUse: /multiserver START §8<§ctemplate§8>"));
                } else if (type.equals(MultiServerCommandType.STOP)) {
                    sender.sendMessage(MultiServerData.text("§cUse: /multiserver STOP §8<§cspace§8>"));
                } else if (type.equals(MultiServerCommandType.CONNECT)) {
                    sender.sendMessage(MultiServerData.text("§cUse: /multiserver CONNECT §8<§cspace§8>"));
                } else if (type.equals(MultiServerCommandType.SPACES)) {
                    MultiServer.instance().templateFactory().templates().forEach(template -> {
                        int[] templateOnline = {0};
                        var spaces = MultiServer.instance().spaceFactory().spaces().stream().filter(it -> it.template().equals(template)).toList();
                        spaces.forEach(space -> {
                            for (Instance instance : space.instances()) {
                                templateOnline[0] += instance.getPlayers().size();
                            }
                        });

                        player.sendMessage(MultiServerData.text(""));
                        player.sendMessage(MultiServerData.text("§9§l" + template.configuration().name() + " §8(§9§l" + templateOnline[0] + "§8)"));

                        if (spaces.isEmpty()) {
                            player.sendMessage(MultiServerData.text("§8• §cNo spaces available."));
                            return;
                        }

                        spaces.forEach(space -> {
                            var isOnSpace = space.instances().stream().anyMatch(it -> it.getPlayers().contains(player));
                            int[] online = {0};
                            space.instances().forEach(it -> online[0] += it.getPlayers().size());

                            player.sendMessage(MultiServerData.text("§8• §7" + space.name() + (isOnSpace ? " §8| §7<-" : ""))
                                    .clickEvent(ClickEvent.runCommand("/multiserver CONNECT " + space.name()))
                                    .hoverEvent(HoverEvent.showText(Component.text("§7Click to connect to §9§l" + space.name()).append(
                                            Component.text("\n§8• §7" + online[0] + " players")
                                    ))));
                        });
                    });
                    var osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

                    player.sendMessage(MultiServerData.text(""));
                    player.sendMessage(MultiServerData.text("§c" + (int) (MinecraftServer.getBenchmarkManager().getUsedMemory() / 1024.0 / 1024.0) + "mb §7& §c" +
                            (int) (osBean.getProcessCpuLoad() * 100)
                            + "% usage §7with §c" + Tick.SERVER_TICKS.getTicksPerSecond() + "ticks/s"));
                    player.sendMessage(MultiServerData.text(""));
                }
            }
        }, typeArgument);


        var valueArgument = ArgumentType.String("value").setSuggestionCallback((sender, context, suggestion) -> {
            if(context.get(typeArgument).equals(MultiServerCommandType.CONNECT) || context.get(typeArgument).equals(MultiServerCommandType.STOP)) {
                MultiServer.instance().spaceFactory().spaces().forEach(space -> {
                    suggestion.addEntry(new SuggestionEntry(space.name()));
                });
            } else if(context.get(typeArgument).equals(MultiServerCommandType.START)) {
                MultiServer.instance().templateFactory().templates().forEach(template -> {
                    suggestion.addEntry(new SuggestionEntry(template.configuration().name()));
                });
            }
        });
        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                var type = context.get(typeArgument);
                var value = context.get(valueArgument);

                if (type.equals(MultiServerCommandType.START)) {
                    var templateOptional = MultiServer.instance().templateFactory().templates()
                            .stream()
                            .filter(it -> it.configuration().name().equalsIgnoreCase(value))
                            .findFirst();
                    if (templateOptional.isEmpty()) {
                        sender.sendMessage(MultiServerData.text("§cTemplate not found!"));
                        return;
                    }

                    var space = MultiServer.instance().spaceFactory().execute(templateOptional.get());
                    sender.sendMessage(MultiServerData.text("§7Running space with following name: §9" + space.name()));
                } else if (type.equals(MultiServerCommandType.STOP)) {
                    var spaceOptional = MultiServer.instance().spaceFactory().spaces().stream().filter(it -> it.name().equalsIgnoreCase(value)).findFirst();
                    if (spaceOptional.isEmpty()) {
                        sender.sendMessage(MultiServerData.text("§cSpace not found!"));
                        return;
                    }

                    var space = spaceOptional.get();
                    MultiServer.instance().spaceFactory().shutdown(space);
                    sender.sendMessage(MultiServerData.text("§7Stopping space with following name: §9" + space.name()));
                } else if (type.equals(MultiServerCommandType.CONNECT)) {
                    var spaceOptional = MultiServer.instance().spaceFactory().spaces().stream().filter(it -> it.name().equalsIgnoreCase(value)).findFirst();
                    if (spaceOptional.isEmpty()) {
                        sender.sendMessage(MultiServerData.text("§cSpace not found!"));
                        return;
                    }

                    var spaceInstance = spaceOptional.get();
                    sender.sendMessage(MultiServerData.text("§7Connecting to space: §9§l" + spaceInstance.name()));
                    if (spaceInstance.spawnInstance() == null) {
                        player.sendMessage(MultiServerData.text("§cNo spawn instance available!"));
                        return;
                    }
                    if (spaceInstance.spawnInstance() == player.getInstance()) {
                        player.sendMessage(MultiServerData.text("§cAlready connected to this space!"));
                        return;
                    }
                    player.setInstance(spaceInstance.spawnInstance());
                } else if (type.equals(MultiServerCommandType.SPACES)) {
                    sender.sendMessage(MultiServerData.text("§cUse: /multiserver SPACES"));
                }
            }
        }, typeArgument, valueArgument);

        MinecraftServer.getCommandManager().register(this);
    }
}