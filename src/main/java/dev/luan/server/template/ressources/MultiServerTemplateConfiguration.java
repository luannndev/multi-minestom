package dev.luan.server.template.ressources;

import dev.luan.server.template.type.MultiServerTemplateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class MultiServerTemplateConfiguration {
    private final String name;
    private final int minOnline;

    private final MultiServerTemplateType type;
}
