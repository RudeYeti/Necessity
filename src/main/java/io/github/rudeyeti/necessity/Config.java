package io.github.rudeyeti.necessity;

import org.bukkit.configuration.Configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {

    public static Configuration config;

    public static class get {
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
        return "The " + option + " value in the configuration must be " + message;
    }

    public static void updateConfig() {
        config = Necessity.plugin.getConfig();

        for (Field field : variables) {
            try {
                field.set(field.getType(), config.get(format(field)));
            } catch (IllegalAccessException error) {
                error.printStackTrace();
            }
        }
    }

    public static boolean validateConfig() {
        boolean isValid = true;

        for (Field field : variables) {
            String option = format(field);
            String configType = config.get(option).getClass().getSimpleName().toLowerCase();
            String fieldType = field.getType().getSimpleName().toLowerCase();

            if (!configType.equals(fieldType)) {
                isValid = false;

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

            if (!option.equals("prefix") && config.get(option) instanceof String && config.getString(option).contains("#")) {
                boolean isBTM = option.equals("build-team-members");

                if (!isBTM || isBTM && config.get("legacy-mode") instanceof Boolean && config.getBoolean("legacy-mode")) {
                    isValid = false;
                    Necessity.logger.severe(message(option, "modified from " + config.get(option) + "."));
                }
            }
        }
        return isValid;
    }
}
