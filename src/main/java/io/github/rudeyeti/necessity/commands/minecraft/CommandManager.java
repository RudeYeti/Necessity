package io.github.rudeyeti.necessity.commands.minecraft;

import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public static boolean executeSubcommands(String command, Map<String, List<String>> subcommands, Map<String, Runnable> executor) {
        AtomicBoolean hasExecuted = new AtomicBoolean(false);

        executor.forEach((name, execute) -> {
            if (command.matches(subcommands.get(name).get(1))) {
                execute.run();
                hasExecuted.set(true);
            }
        });

        return hasExecuted.get();
    }

    public static String usage(String label, Map<String, List<String>> subcommands) {
        final String[] args = {""};

        subcommands.forEach((name, info) -> {
            args[0] += name + " | ";
        });

        args[0] = removeLast(args[0], " \\| ");

        return "Usage: /" + label + " <" + args[0] + ">";
    }

    public static List<String> tabComplete(boolean isReady, String[] args, Map<String, List<String>> subcommands) {
        // Return an empty list instead of null, so the player name does not keep appearing in the command arguments.
        if (isReady) {
            return StringUtil.copyPartialMatches(args[0], new ArrayList<>(subcommands.keySet()), new ArrayList<>());
        } else {
            return Collections.singletonList("");
        }
    }
}
