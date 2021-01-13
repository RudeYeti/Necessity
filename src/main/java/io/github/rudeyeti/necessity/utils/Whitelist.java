package io.github.rudeyeti.necessity.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.commands.discord.CheckCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class Whitelist {

    public static Message message;
    public static TextChannel textChannel;
    public static String messageContent;
    public static AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();

    public static void add(GuildMessageReceivedEvent event) {
        if (event.getGuild() == Necessity.guild && !event.getAuthor().isBot()) {
            message = event.getMessage();
            textChannel = message.getTextChannel();
            messageContent = message.getContentRaw();

            if (event.getChannel().getId().equals(Config.get.whitelistChannelId)) {
                for (String string : Config.get.blacklist) {
                    if (string.equals(messageContent)) {
                        message.delete().queue();
                        textChannel.sendMessage("The specified user `" + messageContent + "` is blacklisted.").queue((message) -> {
                            message.delete().queueAfter(3, TimeUnit.SECONDS);
                        });
                        return;
                    }
                }

                if (Player.exists(messageContent)) {
                    OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(messageContent);

                    if (offlinePlayer.isWhitelisted()) {
                        message.delete().queue();
                        textChannel.sendMessage("The specified user `" + messageContent + "` is already whitelisted.").queue((message) -> {
                            message.delete().queueAfter(3, TimeUnit.SECONDS);
                        });
                    } else {
                        offlinePlayer.setWhitelisted(true);
                        Necessity.server.reloadWhitelist();
                        message.addReaction("✅").queue();
                    }

                    if (Config.get.linkAccounts) {
                        if (DiscordSRV.config().getBoolean("GroupRoleSynchronizationOnLink")) {
                            try {
                                File dataFolder = DiscordSRV.getPlugin().getDataFolder();
                                File file = new File(dataFolder, "synchronization.yml");
                                File backup = new File(dataFolder, "synchronization.yml.old");
                                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                                Files.copy(file.toPath(), backup.toPath());
                                config.set("GroupRoleSynchronizationOnLink", false);
                                config.save(file);
                                DiscordSRV.getPlugin().reloadConfig();

                                accountLinkManager.link(event.getAuthor().getId(), offlinePlayer.getUniqueId());

                                Files.delete(file.toPath());
                                backup.renameTo(file);
                                DiscordSRV.getPlugin().reloadConfig();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            accountLinkManager.link(event.getAuthor().getId(), offlinePlayer.getUniqueId());
                        }
                    }
                } else {
                    message.delete().queue();
                    textChannel.sendMessage("The specified user `" + messageContent + "` is not an actual player.").queue((message) -> {
                        message.delete().queueAfter(3, TimeUnit.SECONDS);
                    });
                }
            } else {
                CheckCommand.execute();
            }
        }
    }

    public static void remove(GuildMemberRemoveEvent event) {
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
