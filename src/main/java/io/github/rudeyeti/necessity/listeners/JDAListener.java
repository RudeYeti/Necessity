package io.github.rudeyeti.necessity.listeners;

import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.commands.discord.CommandManager;
import io.github.rudeyeti.necessity.modules.integration.Integration;
import io.github.rudeyeti.necessity.modules.schematics.Schematics;
import io.github.rudeyeti.necessity.modules.whitelist.Whitelist;
import io.github.rudeyeti.necessity.utils.Control;
import org.jetbrains.annotations.NotNull;

public class JDAListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (Config.get.globalRoleChanges) {
            Integration.syncAllUsers();
        } else if (event.getMember() == Necessity.lastRoleChange) {
            Integration.addRole(Necessity.lastRoleChange);
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            Schematics.get(event);
            Whitelist.add(event);
            CommandManager.execute(event);
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Whitelist.remove(event);
    }
}
