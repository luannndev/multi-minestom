package dev.luan.server.space;

import dev.luan.server.template.MultiServerTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(fluent = true)
public class MultiServerSpace {
    private final String name;
    private final MultiServerTemplate template;

    private Instance spawnInstance;

    private final List<Instance> instances = new ArrayList<>();
}
