package io.github.rudeyeti.necessity.commands.discord;

import io.github.rudeyeti.necessity.Config;

public class IpCommand {
    protected static void execute() {
        String message = "Server Address: `" + Config.get.serverAddress + "`";
        CommandManager.textChannel.sendMessage(message).queue();
    }
}
