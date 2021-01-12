package io.github.rudeyeti.necessity.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.utils.Player;
import io.github.rudeyeti.necessity.utils.Whitelist;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class CheckCommand {
    public static void execute() {
        if (Whitelist.messageContent.startsWith(Config.prefix + "check")) {
            String user = Whitelist.messageContent.replace(Config.prefix + "check" + " ", "");

            if (!user.equals(Whitelist.messageContent)) {
                if (Player.exists(user)) {
                    OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(user);
                    String discordId = Whitelist.accountLinkManager.getDiscordId(offlinePlayer.getUniqueId());

                    if (discordId != null) {
                        Member member = Necessity.guild.getMemberById(discordId);

                        Whitelist.textChannel.sendMessage("Discord Username: `" + member.getUser().getAsTag() + "`").queue();
                        return;
                    }
                } else {
                    if (!Whitelist.message.getMentionedUsers().isEmpty()) {
                        user = Whitelist.message.getMentionedUsers().get(0).getId();
                    }

                    UUID minecraftUuid = Whitelist.accountLinkManager.getUuid(user);

                    if (minecraftUuid != null) {
                        OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(minecraftUuid);

                        Whitelist.textChannel.sendMessage("Minecraft Username: `" + offlinePlayer.getName() + "`").queue();
                        return;
                    }
                }
            } else {
                Whitelist.textChannel.sendMessage("Usage: `" + Config.prefix + "check <discord-id | minecraft-username>`").queue();
                return;
            }

            Whitelist.textChannel.sendMessage("The specified user could not be found.").queue();
        }
    }
}
