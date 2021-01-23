package io.github.rudeyeti.necessity.modules.whitelist;

import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import io.github.rudeyeti.necessity.modules.ModuleManager;

public class Whitelist {

    public static boolean isEnabled() {
        return ModuleManager.isEnabled("Whitelist").containsKey(true);
    }

    public static void add(GuildMessageReceivedEvent event) {
        if (isEnabled()) {
            Member.add(event);
        }
    }

    public static void remove(GuildMemberRemoveEvent event) {
        if (isEnabled()) {
            Member.remove(event);
        }
    }
}
