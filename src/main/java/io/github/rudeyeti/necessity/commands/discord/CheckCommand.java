package io.github.rudeyeti.necessity.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.utils.Player;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public class CheckCommand {
    protected static void execute(List<String> args) {
        if (args.size() > 1) {
            args.stream().skip(1).forEach((arg) -> {
                args.set(0, args.get(0) + " " + arg);
            });
        }

        boolean isUuid = Player.isUuid(args.get(0));

        if (Player.exists(args.get(0)) || isUuid) {
            String discordId;

            if (isUuid) {
                discordId = CommandManager.accountLinkManager.getDiscordId(UUID.fromString(args.get(0)));
            } else {
                OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(args.get(0));
                discordId = CommandManager.accountLinkManager.getDiscordId(offlinePlayer.getUniqueId());
            }

            if (discordId != null) {
                Member member = Necessity.guild.getMemberById(discordId);
                CommandManager.textChannel.sendMessage("Discord Username: `" + member.getUser().getAsTag() + "`\n" +
                                                            "Discord ID: `" + discordId + "`").queue();
                return;
            }
        } else {
            try {
                args.set(0, Necessity.guild.getMemberByTag(args.get(0)).getId());
            } catch (IllegalArgumentException error) {
                if (!CommandManager.message.getMentionedUsers().isEmpty()) {
                    args.set(0, CommandManager.message.getMentionedUsers().get(0).getId());
                }
            }

            UUID minecraftUuid = CommandManager.accountLinkManager.getUuid(args.get(0));

            if (minecraftUuid != null) {
                OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(minecraftUuid);
                CommandManager.textChannel.sendMessage("Minecraft Username: `" + offlinePlayer.getName() + "`\n" +
                                                            "Minecraft UUID: `" + minecraftUuid + "`").queue();
                return;
            }
        }

        CommandManager.textChannel.sendMessage("The specified user could not be found.").queue();
    }
}
