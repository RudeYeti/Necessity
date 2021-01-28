package io.github.rudeyeti.necessity.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Plugins;
import io.github.rudeyeti.necessity.modules.schematics.Schematics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchematicsCommand {

    protected static List<String> message = new ArrayList<String>() {{
        add("A file needs to be specified.");
        add("The specified file could not be found.");
    }};

    private static void addExtension(List<String> args, int index) {
        if (!args.get(index).endsWith(".schematic")) {
            args.set(index, args.get(index) + ".schematic");
        }
    }

    protected static void execute(List<String> args) {
        File schematicsFolder = new File(Plugins.getWorldEdit().getDataFolder() + File.separator + "schematics");

        switch (args.get(0).toLowerCase()) {
            case "list":
                EmbedBuilder embedBuilder = new EmbedBuilder();
                StringBuilder schematicsList = new StringBuilder();
                String[] schematics = schematicsFolder.list();

                if (schematics.length > 0) {
                    for (String fileName : schematics) {
                        schematicsList.append(fileName).append("\n");
                    }

                    // Discord has an embed field character limit of 1024.
                    if (schematicsList.length() > 1024) {
                        int length = schematicsList.length();

                        schematicsList.delete(1017, length);
                        schematicsList.delete(schematicsList.lastIndexOf("\n"), length);
                        schematicsList.append("\nMore...");
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
                break;
            case "download":
                if (args.size() > 1) {
                    args.stream().skip(2).forEach((arg) -> {
                        args.set(1, args.get(1) + " " + arg);
                    });

                    addExtension(args, 1);

                    File file = new File(schematicsFolder, args.get(1));

                    if (file.exists()) {
                        if (file.length() > DiscordUtil.getJda().getSelfUser().getAllowedFileSize()) {
                            CommandManager.textChannel.sendMessage("The specified file exceeds the max file size limit.").queue();
                        } else {
                            CommandManager.textChannel.sendFile(file).queue();
                        }
                    } else {
                        CommandManager.textChannel.sendMessage(message.get(1)).queue();
                    }
                } else {
                    CommandManager.textChannel.sendMessage(message.get(0)).queue();
                }
                break;
            case "rename":
                if (args.size() > 2) {
                    addExtension(args, 1);
                    addExtension(args, 2);

                    File file = new File(schematicsFolder, args.get(1));
                    File name = new File(schematicsFolder, args.get(2));

                    if (file.exists()) {
                        if (!name.exists()) {
                            file.renameTo(name);
                            CommandManager.textChannel.sendMessage("The file `" + args.get(1) + "` has been renamed to `" + args.get(2) + "`.").queue();
                        } else {
                            CommandManager.textChannel.sendMessage("The specified file name already exists.").queue();
                        }
                    } else {
                        CommandManager.textChannel.sendMessage(message.get(1)).queue();
                    }
                } else {
                    CommandManager.textChannel.sendMessage(message.get(0)).queue();
                }
                break;
            default:
                if (Config.get.schematicsCommandMode && args.get(0).equalsIgnoreCase("upload")) {
                    String urlString = args.size() > 1 ? args.get(1) : "";
                    Schematics.get(true, CommandManager.message, urlString);
                    return;
                }

                CommandManager.textChannel.sendMessage("Usage: `" + Config.get.prefix + "schematics " + CommandManager.arguments.get("schematics") + "`").queue();
        }
    }
}
