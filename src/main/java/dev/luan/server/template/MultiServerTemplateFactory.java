package dev.luan.server.template;

import dev.luan.server.GsonUtility;
import dev.luan.server.template.ressources.MultiServerTemplateConfiguration;
import dev.luan.server.template.type.MultiServerTemplateType;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Accessors(fluent = true)
public class MultiServerTemplateFactory {
    private final List<MultiServerTemplate> templates;

    @SneakyThrows
    public MultiServerTemplateFactory() {
        this.templates = new ArrayList<>();

        var tasksFolder = Path.of("tasks");
        if (!tasksFolder.toFile().exists()) {
            log.info("Creating tasks folder...");
            Files.createDirectory(tasksFolder);
        }
        tasksFolder.resolve("global").toFile().mkdirs();

        for (File file : tasksFolder.toFile().listFiles()) {
            if(!file.isFile() && !file.getName().equalsIgnoreCase("global")) {
                var path = tasksFolder.resolve(file.getName());
                GsonUtility.writeIfNotExists(new MultiServerTemplateConfiguration(file.getName().toUpperCase().charAt(0) + file.getName().toLowerCase().substring(1), 0, MultiServerTemplateType.GAME), path);
                this.templates.add(new MultiServerTemplate(file.getName(), GsonUtility.read(MultiServerTemplateConfiguration.class, path)));
            }
        }
    }
}
