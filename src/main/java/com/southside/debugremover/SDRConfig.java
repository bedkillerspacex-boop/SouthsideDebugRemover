package com.southside.debugremover;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SDRConfig {
    private static final Path CONFIG_PATH = Path.of("config", "southsidedebugremover.properties");
    private static final Properties properties = new Properties();

    public static boolean blockerEnabled = true;
    public static boolean debugEnabled = true;
    public static String filterPrefix = "[S]";

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
                properties.load(input);
                blockerEnabled = Boolean.parseBoolean(properties.getProperty("blockerEnabled", "true"));
                debugEnabled = Boolean.parseBoolean(properties.getProperty("debugEnabled", "true"));
                filterPrefix = properties.getProperty("filterPrefix", "[S]");
            } catch (Exception e) {
                SouthsideDebugRemover.LOGGER.error("Failed to load config", e);
            }
        } else {
            save(); // Create default config file
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            properties.setProperty("blockerEnabled", String.valueOf(blockerEnabled));
            properties.setProperty("debugEnabled", String.valueOf(debugEnabled));
            properties.setProperty("filterPrefix", filterPrefix);

            try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
                properties.store(output, "SouthsideDebugRemover Config");
            }
        } catch (IOException e) {
            SouthsideDebugRemover.LOGGER.error("Failed to save config", e);
        }
    }
}
