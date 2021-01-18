package io.github.rudeyeti.necessity.commands.minecraft;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class CommandManager {
    private static String removeLast(String string, String regex) {
        String reverse = new StringBuilder(string).reverse().toString().replaceFirst(regex, "");
        return new StringBuilder(reverse).reverse().toString();
    }

    public static String listSubcommands(Map<String, List<String>> subcommands) {
        final String[] list = {"List of available subcommands:\n"};

        subcommands.forEach((name, info) -> {
            list[0] += name + " - " + info.get(0) + "\n";
        });

        list[0] = removeLast(list[0], "\n");

        return list[0];
    }

    public static String usage(String label, Map<String, List<String>> subcommands) {
        final String[] args = {""};

        subcommands.forEach((name, info) -> {
            args[0] += name + " | ";
        });

        args[0] = removeLast(args[0], " \\| ");

        return "Usage: /" + label + " <" + args[0] + ">";
    }
}
