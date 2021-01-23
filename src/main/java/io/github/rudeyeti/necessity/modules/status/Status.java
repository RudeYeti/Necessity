package io.github.rudeyeti.necessity.modules.status;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.modules.ModuleManager;

import java.util.ArrayList;
import java.util.List;

public class Status {

    public static TextChannel statusChannel;
    public static List<String> onlinePlayers = new ArrayList<>();

    public static boolean isEnabled() {
        return ModuleManager.isEnabled("Status").containsKey(true);
    }

    public static void initialize() {
        if (isEnabled()) {
            Messages.initialize();
        }
    }

    public static void delete() {
        if (!Config.get.messageId.isEmpty()) {
            Status.statusChannel.deleteMessageById(Config.get.messageId).queue();
            Config.setValue("message-id", Config.get.messageId, "");
            Config.updateConfig();
        }
    }

    public static EmbedBuilder serverOn() {
        return isEnabled() ? Messages.serverOn() : null;
    }

    public static EmbedBuilder serverOff() {
        return isEnabled() ? Messages.serverOff() : null;
    }
}
