package dev.luan.server.template;

import dev.luan.server.task.MultiServerTask;
import dev.luan.server.template.ressources.MultiServerTemplateConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class MultiServerTemplate {
    private final String folderName;
    private final MultiServerTemplateConfiguration configuration;

    private final List<MultiServerTask> tasks = new ArrayList<>();
}