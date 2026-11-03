package dev.medbeebs.monoplegia;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MonoplegiaConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("monoplegia");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("monoplegia.json");
    public static final boolean DEFAULT_BLOCK_OFFHAND_LIGHTS = false;
    public static final Notification DEFAULT_NOTIFICATION = Notification.ACTION_BAR;
    public boolean blockOffhandLights = DEFAULT_BLOCK_OFFHAND_LIGHTS;
    public Notification notification = DEFAULT_NOTIFICATION;

    public enum Notification { ACTION_BAR, CHAT }

    public static MonoplegiaConfig load() {
        if (!Files.exists(PATH)) return new MonoplegiaConfig();
        try (Reader reader = Files.newBufferedReader(PATH)) {
            MonoplegiaConfig config = GSON.fromJson(reader, MonoplegiaConfig.class);
            if (config == null || config.notification == null) {
                throw new IllegalArgumentException("Invalid or incomplete config");
            }
            return config;
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Cannot read {}; using defaults. Original saved as .invalid when possible.", PATH, exception);
            try {
                Files.copy(PATH, PATH.resolveSibling("monoplegia.json.invalid"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException backupException) {
                LOGGER.warn("Cannot back up invalid config", backupException);
            }
            return new MonoplegiaConfig();
        }
    }

    public void save() {
        Path temporary = PATH.resolveSibling("monoplegia.json.tmp");
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(temporary, GSON.toJson(this) + System.lineSeparator());
            Files.move(temporary, PATH, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            LOGGER.error("Cannot save {}. Current settings remain active for this session.", PATH, exception);
        }
    }
}
