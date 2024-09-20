package dev.luan.server;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@UtilityClass
public final class MultiServerData {

    public Component text(String text) {
        return MiniMessage.miniMessage().deserialize("<dark_gray>[</dark_gray><gradient:#1dde50:#0c7528>ᴍᴜʟᴛɪsᴇʀᴠᴇʀ</gradient><dark_gray>]</dark_gray>").append(Component.text("§7 " + text));
    }
}
