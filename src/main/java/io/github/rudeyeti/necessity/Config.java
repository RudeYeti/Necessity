package io.github.rudeyeti.necessity;

import org.bukkit.configuration.Configuration;

import java.lang.reflect.Field;
import java.util.*;

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
        public static String schematicsChannelId;
        public static String sizeLimit;
        // Status Configuration
        public static String statusChannelId;
        public static String serverAddress;
        public static String messageId;
    }

    public static List<Field> variables = Arrays.asList(get.class.getDeclaredFields());

    private static String format(Field field) {
        return field.getName().replaceAll("([A-Z])", "-$1").toLowerCase();
    }

    private static String message(String option, String message) {
        return "Usage: The " + option + " value in the configuration must be " + message;
    }

    public static void updateConfig() {
        config = Necessity.plugin.getConfig();

        for (Field field : variables) {
            try {
                field.set(field.getType(), config.get(format(field)));
            } catch (IllegalArgumentException ignored) {
            } catch (IllegalAccessException error) {
                error.printStackTrace();
            }
        }
    }

    public static boolean validateConfig(boolean logToConsole) {
        boolean isValid = true;

        for (Field field : variables) {
            String option = format(field);
            Object value = config.get(option);
            String configType = value.getClass().getSimpleName().toLowerCase();
            String fieldType = field.getType().getSimpleName().toLowerCase();

            if (!configType.equals(fieldType) || value instanceof String && ((String) value).contains("\"")) {
                isValid = false;

                if (logToConsole) {
                    switch (fieldType) {
                        case "boolean":
                            Necessity.logger.severe(message(option, "either true or false."));
                            break;
                        case "string":
                            Necessity.logger.severe(message(option, "enclosed in quotes."));
                            break;
                        case "arraylist":
                            Necessity.logger.severe(message(option, "a list."));
                            break;
                        default:
                            Necessity.logger.severe(message(option, "of type " + fieldType + "."));
                            break;
                    }
                }
            }

            if (!option.equals("prefix") && value instanceof String && ((String) value).contains("#")) {
                boolean isBTM = option.equals("build-team-members");

                if (!isBTM || config.get("legacy-mode") instanceof Boolean && config.getBoolean("legacy-mode")) {
                    isValid = false;

                    if (logToConsole) {
                        Necessity.logger.severe(message(option, "modified from " + value + "."));
                    }
                }
            }
        }

        return isValid;
    }

    public static Map<String, Object> getValues(boolean getValid) {
        Map<String, Object> hashMap = new HashMap<>();

        for (Field field : variables) {
            String option = format(field);
            Object value = config.get(option);
            String configType = value.getClass().getSimpleName().toLowerCase();
            String fieldType = field.getType().getSimpleName().toLowerCase();

            if (!getValid && (!configType.equals(fieldType) || value instanceof String && ((String) value).contains("\""))) {
                hashMap.put(option, value);
            } else if (getValid) {
                hashMap.put(option, value);
            }

            if (!option.equals("prefix") && value instanceof String && ((String) value).contains("#")) {
                boolean isBTM = option.equals("build-team-members");

                if (!getValid && (!isBTM || config.get("legacy-mode") instanceof Boolean && config.getBoolean("legacy-mode"))) {
                    hashMap.put(option, value);
                }
            }
        }

        return hashMap;
    }

}
