package io.github.rudeyeti.necessity.modules.schematics;

import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import io.github.rudeyeti.necessity.modules.ModuleManager;

import java.net.URL;

public class Schematics {

    public static boolean isEnabled() {
        return ModuleManager.isEnabled("Schematics").containsKey(true);
    }

    public static String download(URL url, java.io.File destFolder) {
        return isEnabled() ? File.download(url, destFolder) : null;
    }

    public static void get(GuildMessageReceivedEvent event) {
        if (isEnabled()) {
            File.get(event);
        }
    }
}
