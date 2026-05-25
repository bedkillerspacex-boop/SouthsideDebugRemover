package com.southside.debugremover;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Properties;

public class SDRConfig {
    private static final Path CONFIG_PATH = Path.of("config", "southsidedebugremover.properties");
    private static final Properties properties = new Properties();

    private static final String DEFAULT_FILTER_RULES = "[S]";
    private static final String DEFAULT_WHITELIST = "[S] [IRC]\n[S] Bound";

    public static boolean blockerEnabled = true;
    public static boolean debugEnabled = true;
    public static String filterRules = DEFAULT_FILTER_RULES;
    public static String whitelist = DEFAULT_WHITELIST;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
                properties.load(input);
                blockerEnabled = Boolean.parseBoolean(properties.getProperty("blockerEnabled", "true"));
                debugEnabled = Boolean.parseBoolean(properties.getProperty("debugEnabled", "true"));
                
                // Load filter rules
                String savedRules = properties.getProperty("filterRules", "");
                if (!savedRules.isEmpty()) {
                    try {
                        filterRules = new String(Base64.getDecoder().decode(savedRules), StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        filterRules = savedRules.replace("\\n", "\n");
                    }
                } else {
                    filterRules = DEFAULT_FILTER_RULES;
                }

                // Load whitelist
                String savedWhitelist = properties.getProperty("whitelist", "");
                if (!savedWhitelist.isEmpty()) {
                    try {
                        whitelist = new String(Base64.getDecoder().decode(savedWhitelist), StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        whitelist = savedWhitelist.replace("\\n", "\n");
                    }
                } else {
                    whitelist = DEFAULT_WHITELIST;
                }
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
            
            // Base64 encode the multiline filterRules
            String encodedRules = Base64.getEncoder().encodeToString(filterRules.getBytes(StandardCharsets.UTF_8));
            properties.setProperty("filterRules", encodedRules);
            
            // Base64 encode the multiline whitelist
            String encodedWhitelist = Base64.getEncoder().encodeToString(whitelist.getBytes(StandardCharsets.UTF_8));
            properties.setProperty("whitelist", encodedWhitelist);

            try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
                properties.store(output, "SouthsideDebugRemover Config");
            }
        } catch (IOException e) {
            SouthsideDebugRemover.LOGGER.error("Failed to save config", e);
        }
    }

    public static String[] getFilterRulesLines() {
        if (filterRules == null || filterRules.isEmpty()) {
            return new String[0];
        }
        return filterRules.split("\\r?\\n");
    }

    public static String[] getWhitelistLines() {
        if (whitelist == null || whitelist.isEmpty()) {
            return new String[0];
        }
        return whitelist.split("\\r?\\n");
    }
}
