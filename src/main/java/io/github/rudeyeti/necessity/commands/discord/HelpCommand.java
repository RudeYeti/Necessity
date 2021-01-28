package io.github.rudeyeti.necessity.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import io.github.rudeyeti.necessity.Config;

import java.util.List;

public class HelpCommand {
    protected static void execute(List<String> args) {
        String lowercase = args.get(0).toLowerCase();

        if (lowercase.equals("list")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            StringBuilder stringBuilder = new StringBuilder();

            CommandManager.commands.keySet().stream().skip(1).forEach((name) -> {
                String message = "`" + Config.get.prefix + name + "`\n";
                stringBuilder.append(message);
            });

            embedBuilder.addField(
                    "Help:",
                    stringBuilder.toString(),
                    false
            );

            CommandManager.textChannel.sendMessage(embedBuilder.build()).queue();
        } else if (CommandManager.commands.containsKey(lowercase)) {
            String arguments = CommandManager.arguments.get(lowercase);
            String message = "`" + Config.get.prefix + lowercase;

            if (arguments != null) {
                message += " " + arguments;
            }

            CommandManager.textChannel.sendMessage("Help: " + message + "`").queue();
        } else {
            CommandManager.textChannel.sendMessage("Usage: The specified command must be an actual command.").queue();
        }
    }
}