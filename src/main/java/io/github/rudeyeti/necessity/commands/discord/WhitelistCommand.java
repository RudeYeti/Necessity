package io.github.rudeyeti.necessity.commands.discord;

import io.github.rudeyeti.necessity.modules.whitelist.Whitelist;

import java.util.List;

public class WhitelistCommand {
    protected static void execute(List<String> args) {
        if (args.size() > 1) {
            args.stream().skip(1).forEach((arg) -> {
                args.set(0, args.get(0) + " " + arg);
            });
        }

        Whitelist.add(true, CommandManager.message, args.get(0));
    }
}
