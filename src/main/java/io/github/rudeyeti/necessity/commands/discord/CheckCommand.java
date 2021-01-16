package io.github.rudeyeti.necessity.commands.discord;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.modules.whitelist.Member;
import io.github.rudeyeti.necessity.modules.whitelist.Player;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class CheckCommand {
    public static void execute() {
        if (Member.messageContent.startsWith(Config.get.prefix + "check")) {
            String user = Member.messageContent.replace(Config.get.prefix + "check" + " ", "");

            if (!user.equals(Member.messageContent)) {
                if (Player.exists(user)) {
                    OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(user);
                    String discordId = Member.accountLinkManager.getDiscordId(offlinePlayer.getUniqueId());

                    if (discordId != null) {
                        github.scarsz.discordsrv.dependencies.jda.api.entities.Member member = Necessity.guild.getMemberById(discordId);

                        Member.textChannel.sendMessage("Discord Username: `" + member.getUser().getAsTag() + "`").queue();
                        return;
                    }
                } else {
                    if (!Member.message.getMentionedUsers().isEmpty()) {
                        user = Member.message.getMentionedUsers().get(0).getId();
                    }

                    UUID minecraftUuid = Member.accountLinkManager.getUuid(user);

                    if (minecraftUuid != null) {
                        OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(minecraftUuid);

                        Member.textChannel.sendMessage("Minecraft Username: `" + offlinePlayer.getName() + "`").queue();
                        return;
                    }
                }
            } else {
                Member.textChannel.sendMessage("Usage: `" + Config.get.prefix + "check <discord-id | minecraft-username>`").queue();
                return;
            }

            Member.textChannel.sendMessage("The specified user could not be found.").queue();
        }
    }
}
