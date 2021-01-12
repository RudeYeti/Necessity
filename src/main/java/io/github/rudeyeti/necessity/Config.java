package io.github.rudeyeti.necessity;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.configuration.Configuration;

import java.util.List;

public class Config {

    public static Configuration config;
    public static String buildTeamMembers;
    public static String discordRoleId;
    public static String minecraftRoleName;
    public static boolean globalRoleChanges;
    public static boolean logRoleChanges;
    public static String whitelistChannelId;
    public static String prefix;
    public static boolean linkAccounts;
    public static boolean deleteOnLeave;
    public static List<String> blacklist;
    public static String schematicsChannelId;
    public static int sizeLimit;
    public static String statusChannelId;
    public static String serverAddress;
    public static String messageId;

    public static void updateConfig() {
        config = Necessity.plugin.getConfig();
        buildTeamMembers = config.getString("build-team-members");
        discordRoleId = config.getString("discord-role-id");
        minecraftRoleName = config.getString("minecraft-role-name");
        globalRoleChanges = config.getBoolean("global-role-changes");
        logRoleChanges = config.getBoolean("log-role-changes");
        whitelistChannelId = config.getString("whitelist-channel-id");
        prefix = config.getString("prefix");
        linkAccounts = config.getBoolean("link-accounts");
        deleteOnLeave = config.getBoolean("delete-on-leave");
        blacklist = config.getStringList("blacklist");
        schematicsChannelId = config.getString("schematics-channel-id");
        sizeLimit = Integer.parseInt(config.getString("size-limit")) * 1000;
        statusChannelId = config.getString("status-channel-id");
        serverAddress = config.getString("server-address");
        messageId = config.getString("message-id");
    }

    private static String message(String option, String message) {
        return "The " + option + " value in the configuration must be " + message;
    }

    public static boolean validateConfig() {
        if (config.get("build-team-members").equals("https://buildtheearth.net/buildteams/#/members")) {
            Necessity.logger.warning(message("build-team-members", "modified from https://buildtheearth.net/buildteams/#/members."));
        } else if (config.get("discord-role-id").equals("##################")) {
            Necessity.logger.warning(message("discord-role-id", "modified from ##################."));
        } else if (!(config.get("global-role-changes") instanceof Boolean)) {
            Necessity.logger.warning(message("global-role-changes", "either true or false."));
        } else if (!(config.get("log-role-changes") instanceof Boolean)) {
            Necessity.logger.warning(message("log-role-changes", "either true or false."));
        } else if (!(config.get("whitelist-channel-id") instanceof String)) {
            Necessity.logger.warning(message("whitelist-channel-id", "enclosed in quotes."));
        } else if (config.get("whitelist-channel-id").equals("##################")) {
            Necessity.logger.warning(message("whitelist-channel-id", "modified from ##################."));
        } else if (!(config.get("prefix") instanceof String)) {
            Necessity.logger.warning(message("prefix", "enclosed in quotes."));
        } else if (!(config.get("link-accounts") instanceof Boolean)) {
            Necessity.logger.warning(message("link-accounts", "either true or false."));
        } else if (!(config.get("delete-on-leave") instanceof Boolean)) {
            Necessity.logger.warning(message("delete-on-leave", "either true or false."));
        } else if (!(config.get("blacklist") instanceof List)) {
            Necessity.logger.warning(message("blacklist", "a list with entries enclosed in quotes."));
        } else if (!(config.get("schematics-channel-id") instanceof String)) {
            Necessity.logger.warning(message("schematics-channel-id", "enclosed in quotes."));
        } else if (config.get("schematics-channel-id").equals("##################")) {
            Necessity.logger.warning(message("schematics-channel-id", "modified from ##################."));
        } else if (!(config.get("size-limit") instanceof String)) {
            Necessity.logger.warning(message("size-limit", "enclosed in quotes."));
        } else if (!NumberUtils.isDigits(config.getString("size-limit"))) {
            Necessity.logger.warning(message("size-limit", "must be a number."));
        } else if (!(config.get("status-channel-id") instanceof String)) {
            Necessity.logger.warning(message("status-channel-id", "enclosed in quotes."));
        } else if (config.get("status-channel-id").equals("##################")) {
            Necessity.logger.warning(message("status-channel-id", "modified from ##################."));
        } else if (!(config.get("server-address") instanceof String)) {
            Necessity.logger.warning(message("server-address", "enclosed in quotes."));
        } else if (config.get("server-address").equals("example.com:25565")) {
            Necessity.logger.warning(message("server-address", "modified from example.com:25565."));
        } else {
            return true;
        }
        return false;
    }
}
