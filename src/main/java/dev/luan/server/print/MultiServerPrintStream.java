package dev.luan.server.print;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;

public final class MultiServerPrintStream extends PrintStream {

    public MultiServerPrintStream(@NotNull OutputStream out) {
        super(out);
    }
}
