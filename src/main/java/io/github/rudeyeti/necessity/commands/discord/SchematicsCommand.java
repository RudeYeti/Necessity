package io.github.rudeyeti.necessity.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Plugins;

import java.io.File;
import java.util.List;

public class SchematicsCommand {
    protected static void execute(List<String> args) {
        File schematicsFolder = new File(Plugins.getWorldEdit().getDataFolder() + java.io.File.separator + "schematics");

        if (args.get(0).equalsIgnoreCase("list")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            StringBuilder schematicsList = new StringBuilder();
            String[] schematics = schematicsFolder.list();

            if (schematics.length > 0) {
                for (String fileName : schematics) {
                    schematicsList.append(fileName + "\n");
                }
            } else {
                schematicsList.append("None");
            }

            embedBuilder.addField(
                    "Schematics:",
                    schematicsList.toString(),
                    false
            );

            CommandManager.textChannel.sendMessage(embedBuilder.build()).queue();
        } else if (args.get(0).equalsIgnoreCase("download")) {
            if (args.size() > 1) {
                args.stream().skip(2).forEach((arg) -> {
                    args.set(1, args.get(1) + " " + arg);
                });

                if (!args.get(1).endsWith(".schematic")) {
                    args.set(1, args.get(1) + ".schematic");
                }

                File file = new File(schematicsFolder, args.get(1));

                if (file.exists()) {
                    if (file.length() > DiscordUtil.getJda().getSelfUser().getAllowedFileSize()) {
                        CommandManager.textChannel.sendMessage("The specified file exceeds the max file size limit.").queue();
                    } else {
                        CommandManager.textChannel.sendFile(file).queue();
                    }
                } else {
                    CommandManager.textChannel.sendMessage("The specified file could not be found.").queue();
                }
            } else {
                CommandManager.textChannel.sendMessage("A file needs to be specified.").queue();
            }
        } else {
            CommandManager.textChannel.sendMessage("Usage: `" + Config.get.prefix + "schematics " + CommandManager.arguments.get("schematics") + "`").queue();
        }
    }
}
