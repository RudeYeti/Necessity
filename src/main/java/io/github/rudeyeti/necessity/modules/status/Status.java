package io.github.rudeyeti.necessity.modules.status;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import io.github.rudeyeti.necessity.modules.ModuleManager;

public class Status {
    public static boolean isEnabled = ModuleManager.isEnabled("Status").containsKey(true);

    public static void initialize() {
        if (isEnabled) {
            Messages.initialize();
        }
    }

    public static EmbedBuilder serverOn() {
        return isEnabled ? Messages.serverOn() : null;
    }

    public static EmbedBuilder serverOff() {
        return isEnabled ? Messages.serverOff() : null;
    }
}
