package dev.luan.server;

import dev.luan.server.server.Server;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Accessors(fluent = true)
public final class MultiServer {

    @Getter
    private static MultiServer instance;

    public MultiServer() {
        instance = this;

        var server = new Server(log::info);
        server.run();
    }
}
