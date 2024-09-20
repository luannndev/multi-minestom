package dev.luan.server.task;

import dev.luan.server.space.MultiServerSpace;
import dev.luan.server.space.type.MultiServerSpaceState;
import dev.luan.server.task.annotation.MultiServerTaskEnvironment;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class MultiServerTask {
    private MultiServerTaskEnvironment environment;

    public void spaceState(MultiServerSpace space, MultiServerSpaceState state) {
    }
}