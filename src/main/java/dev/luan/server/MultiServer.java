package dev.luan.server;

import dev.luan.server.event.MultiServerEventFactory;
import dev.luan.server.instance.MultiServerInstanceFactory;
import dev.luan.server.server.Server;
import dev.luan.server.space.MultiServerSpaceFactory;
import dev.luan.server.space.command.MultiServerCommand;
import dev.luan.server.space.command.MultiServerLobbyCommand;
import dev.luan.server.task.MultiServerTaskFactory;
import dev.luan.server.template.MultiServerTemplateFactory;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
@Accessors(fluent = true)
public final class MultiServer {

    @Getter
    private static MultiServer instance;

    private final MultiServerTemplateFactory templateFactory;
    private final MultiServerTaskFactory taskFactory;
    private final MultiServerSpaceFactory spaceFactory;
    private final MultiServerEventFactory eventFactory;
    private final MultiServerInstanceFactory instanceFactory;


    public MultiServer() {
        instance = this;

        var server = new Server(log::info);
        server.run();

        this.spaceFactory = new MultiServerSpaceFactory();
        this.eventFactory = new MultiServerEventFactory();
        this.instanceFactory = new MultiServerInstanceFactory();
        this.templateFactory = new MultiServerTemplateFactory();
        this.taskFactory = new MultiServerTaskFactory();

        this.templateFactory.templates()
                .stream()
                .filter(it -> it.configuration().minOnline() > 0)
                .forEach(template -> {
                    for (int i = 0; i < template.configuration().minOnline(); i++) {
                        this.spaceFactory.execute(template);
                    }
                });

        new MultiServerCommand();
        new MultiServerLobbyCommand();

        var startup = System.currentTimeMillis() - Long.valueOf(System.getProperty("multiserver.startup"));
        log.info("MultiServer has been started! Took {}ms ({}s)", startup, TimeUnit.MILLISECONDS.toSeconds(startup));
    }
}
