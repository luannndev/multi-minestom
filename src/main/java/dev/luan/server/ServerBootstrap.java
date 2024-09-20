package dev.luan.server;

public final class ServerBootstrap {

    public static void main(String[] args) {
        System.setProperty("multiserver.startup", String.valueOf(System.currentTimeMillis()));
        new MultiServer();
    }
}
