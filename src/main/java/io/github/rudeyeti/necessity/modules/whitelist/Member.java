package io.github.rudeyeti.necessity.modules.whitelist;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.utils.Player;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Member {

    protected static AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();

    private static void getErrorMessage(boolean isCommand, Message message, String errorMessage) {
        if (isCommand) {
            message.getTextChannel().sendMessage(errorMessage).queue();
        } else {
            message.getTextChannel().sendMessage(errorMessage).complete().delete().completeAfter(3, TimeUnit.SECONDS);
            message.delete().queue();
        }
    }

    protected static void add(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getTextChannel().getId().equals(Config.get.whitelistChannelId)) {
            add(false, message, event.getMessage().getContentRaw());
        }
    }

    protected static void add(boolean isCommand, Message message, String user) {
        TextChannel channel = message.getTextChannel();

        if (message.getGuild() == Necessity.guild) {
            if (Config.get.whitelistCommandMode || channel.getId().equals(Config.get.whitelistChannelId)) {
                for (String string : Config.get.blacklist) {
                    if (string.equals(user)) {
                        getErrorMessage(isCommand, message, "Usage: The specified user `" + user + "` is blacklisted.");
                        return;
                    }
                }

                boolean isUuid = Player.isUuid(user);

                if (Player.exists(user) || isUuid) {
                    OfflinePlayer offlinePlayer = isUuid ?
                            Necessity.server.getOfflinePlayer(UUID.fromString(user)) :
                            Necessity.server.getOfflinePlayer(user);

                    if (offlinePlayer.isWhitelisted()) {
                        getErrorMessage(isCommand, message, "Usage: The specified user `" + user + "` is already whitelisted.");
                    } else {
                        offlinePlayer.setWhitelisted(true);
                        Necessity.server.reloadWhitelist();

                        if (isCommand) {
                            channel.sendMessage(String.format("The specified user `%s` has been added to the whitelist.", user)).queue();
                        } else {
                            message.addReaction("✅").queue();
                        }
                    }

                    if (Config.get.linkAccounts) {
                        String option = "GroupRoleSynchronizationOnLink";

                        if (DiscordSRV.config().getBoolean(option)) {
                            try {
                                Path config = new File(DiscordSRV.getPlugin().getDataFolder(), "synchronization.yml").toPath();
                                String content = new String(Files.readAllBytes(config));

                                Config.setValue(config, content, option, true, false);
                                DiscordSRV.getPlugin().reloadConfig();

                                accountLinkManager.link(message.getAuthor().getId(), offlinePlayer.getUniqueId());

                                Config.setValue(config, content, option, false, true);
                                DiscordSRV.getPlugin().reloadConfig();
                            } catch (IOException error) {
                                error.printStackTrace();
                            }
                        } else {
                            accountLinkManager.link(message.getAuthor().getId(), offlinePlayer.getUniqueId());
                        }
                    }
                } else {
                    getErrorMessage(isCommand, message, "Usage: The specified user `" + user + "` is not an actual player.");
                }
            }
        }
    }

    protected static void remove(GuildMemberRemoveEvent event) {
        if (event.getMember().getGuild() == Necessity.guild) {
            TextChannel channel = Necessity.guild.getTextChannelById(Config.get.whitelistChannelId);

            channel.getIterableHistory().cache(false).forEachAsync((message) -> {

                if (message.getAuthor().getId().equals(event.getUser().getId())) {
                    OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(message.getContentRaw());

                    if (Player.exists(message.getContentRaw())) {
                        offlinePlayer.setWhitelisted(false);
                        Necessity.server.reloadWhitelist();

                        if (Config.get.linkAccounts) {
                            accountLinkManager.unlink(event.getUser().getId());
                        }
                    }

                    if (Config.get.deleteOnLeave) {
                        message.delete().queue();
                    } else {
                        message.getReactions().forEach((reaction) ->
                                reaction.removeReaction().queue()
                        );
                        message.addReaction("❌").queue();
                    }
                }

                return true;
            });
        }
    }
}
