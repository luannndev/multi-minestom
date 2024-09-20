package dev.luan.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

@UtilityClass
public final class GsonUtility {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void writeIfNotExists(Object object, Path path) {
        var finalPath = path.resolve(object.getClass().getSimpleName().toLowerCase()
                .replace("multiserver", "")
                .replace("configuration", "") + ".json").toAbsolutePath().toString();
    }

    public void write(Object object, Path path) {
        var finalPath = path.resolve(object.getClass().getSimpleName().toLowerCase()
                .replace("multiserver", "")
                .replace("configuration", "") + ".json");

        try (FileWriter writer = new FileWriter(finalPath.toAbsolutePath().toString())) {
            GSON.toJson(object, writer);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    @SneakyThrows
    public <T> T read(Class<T> clazz, Path path) {
        try (Reader reader = new FileReader(path.resolve(clazz.getSimpleName().toLowerCase()
                .replace("multiserver", "")
                .replace("configuration", "") + ".json").toAbsolutePath().toString())) {
            return GSON.fromJson(reader, clazz);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
