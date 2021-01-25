package io.github.rudeyeti.necessity;

import org.bukkit.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Config {

    public static Configuration config;

    public static class get {
        // Module Configuration
        public static boolean integration;
        public static boolean whitelist;
        public static boolean activity;
        public static boolean schematics;
        public static boolean status;
        // Discord Configuration
        public static String prefix;
        // Integration Configuration
        public static String apiKey;
        public static String discordRoleId;
        public static String minecraftRoleName;
        public static boolean globalRoleChanges;
        public static boolean logRoleChanges;
        // Integration Legacy Mode Configuration
        public static boolean legacyMode;
        public static String buildTeamMembers;
        // Whitelist Configuration
        public static String whitelistChannelId;
        public static boolean linkAccounts;
        public static boolean deleteOnLeave;
        public static ArrayList<String> blacklist;
        // Schematics Configuration
        public static boolean commandMode;
        public static String schematicsChannelId;
        public static String sizeLimit;
        // Status Configuration
        public static String statusChannelId;
        public static String serverAddress;
        // Plugin Constants
        public static String messageId;
        public static boolean maintenance;
    }

    public static List<Field> variables = Arrays.asList(get.class.getDeclaredFields());
    public static final String getMessage = "Usage: The %s value in the configuration must be %s";

    private static boolean getBoolean(int id, List<Object> list) {
        String value = list.get(1) instanceof String ? list.get(1).toString() : "";

        switch (id) {
            case 0:
                return !list.get(2).equals(list.get(3)) || value.contains("\"");
            case 1:
                return !list.get(0).equals("prefix") && value.contains("#") && !list.get(0).equals("build-team-members") ||
                        config.get("legacy-mode") instanceof Boolean && config.getBoolean("legacy-mode");
            default:
                return false;
        }
    }

    private static String format(Field field) {
        return field.getName().replaceAll("([A-Z])", "-$1").toLowerCase();
    }

    private static Object addQuotes(Object object) {
        if (object instanceof String && !((String) object).contains("\"")) {
            return "\"" + object + "\"";
        } else {
            return object;
        }
    }

    private static Map<Field, List<Object>> forEachVariable() {
        Map<Field, List<Object>> hashMap = new LinkedHashMap<>();

        variables.forEach((field) -> {
            String option = format(field);
            Object value = config.get(option);

            hashMap.put(field, Arrays.asList(
                    option,
                    value,
                    value.getClass().getSimpleName().toLowerCase(),
                    field.getType().getSimpleName().toLowerCase()
            ));
        });

        return hashMap;
    }

    public static Map<String, Object> getValues(boolean getValid) {
        Map<String, Object> hashMap = new LinkedHashMap<>();

        forEachVariable().forEach((field, list) -> {
            if (getValid || getBoolean(0, list) || getBoolean(1, list)) {
                hashMap.put(list.get(0).toString(), list.get(1));
            }
        });

        return hashMap;
    }

    public static String setValue(String option, Object newValue, Object oldValue) {
        try {
            Path config = new File(Necessity.plugin.getDataFolder(), "config.yml").toPath();
            String content = new String(Files.readAllBytes(config));

            return setValue(config, content, option, newValue, oldValue);
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    public static String setValue(Path config, String content, String option, Object newValue, Object oldValue) {
        try {
            String key = option + ": ";
            content = content.replace(key + addQuotes(newValue), key + addQuotes(oldValue));

            Files.write(config, content.getBytes());
            Necessity.plugin.reloadConfig();

            return content;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    public static void updateConfig() {
        config = Necessity.plugin.getConfig();

        forEachVariable().forEach((field, list) -> {
            try {
                field.set(field.getType(), list.get(1));
            } catch (IllegalArgumentException ignored) {
            } catch (IllegalAccessException error) {
                error.printStackTrace();
            }
        });
    }

    public static boolean validateConfig(boolean logToConsole) {
        AtomicBoolean isValid = new AtomicBoolean(true);

        forEachVariable().forEach((field, list) -> {
            if (getBoolean(0, list)) {
                isValid.set(false);

                if (logToConsole) {
                    switch (list.get(3).toString()) {
                        case "boolean":
                            Necessity.logger.severe(String.format(getMessage, list.get(0), "either true or false."));
                            break;
                        case "string":
                            Necessity.logger.severe(String.format(getMessage, list.get(0), "enclosed in quotes."));
                            break;
                        case "arraylist":
                            Necessity.logger.severe(String.format(getMessage, list.get(0), "a list."));
                            break;
                    }
                }
            }

            if (getBoolean(1, list)) {
                isValid.set(false);

                if (logToConsole) {
                    Necessity.logger.severe(String.format(getMessage, list.get(0), "modified from " + list.get(1) + "."));
                }
            }
        });

        return isValid.get();
    }
}
