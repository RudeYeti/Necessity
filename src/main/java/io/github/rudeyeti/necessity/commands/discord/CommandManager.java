package io.github.rudeyeti.necessity.commands.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import io.github.rudeyeti.necessity.Config;

import java.util.*;

public class CommandManager {

    public static Map<String, Runnable> commands = new HashMap<>();
    public static Map<String, String> arguments = new HashMap<>();
    public static Message message;
    public static String messageContent;
    public static TextChannel textChannel;
    public static AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();

    public static void execute(GuildMessageReceivedEvent event) {
        List<String> args = new ArrayList<>();
        message = event.getMessage();
        messageContent = message.getContentRaw();
        textChannel = message.getTextChannel();
        String upload = Config.get.schematicsCommandMode ? " | upload" : "";
        String url = Config.get.schematicsCommandMode ? " | url" : "";

        commands.put("check", () -> CheckCommand.execute(args));
        arguments.put("check", "<id | uuid | user | username>");
        commands.put("schematics", () -> SchematicsCommand.execute(args));
        arguments.put("schematics", "<list" + upload + " | download | rename> [file" + url + "] [name]");

        if (Config.get.whitelistCommandMode) {
            commands.put("whitelist", () -> WhitelistCommand.execute(args));
            arguments.put("whitelist", "<uuid | username>");
        }

        if (messageContent.startsWith(Config.get.prefix)) {
            List<String> channelIds = Arrays.asList(Config.get.statusChannelId, Config.get.schematicsChannelId, Config.get.whitelistChannelId);

            if (!channelIds.contains(textChannel.getId())) {
                args.add(messageContent.substring(Config.get.prefix.length()));

                commands.forEach((name, execute) -> {
                    if (args.get(0).startsWith(name)) {
                        args.set(0, args.get(0).substring(name.length()));

                        if (args.get(0).length() > 1) {
                            String substring = args.get(0).substring(1);
                            String[] split = substring.split(" ");

                            if (split.length > 0) {
                                args.addAll(Arrays.asList(split));
                                args.remove(0);
                            } else {
                                args.set(0, substring);
                            }

                            execute.run();
                        } else if (arguments.get(name) == null) {
                            execute.run();
                        } else {
                            textChannel.sendMessage("Usage: `" + Config.get.prefix + name + " " + arguments.get(name) + "`").queue();
                        }
                    }
                });
            }
        }
    }
}
