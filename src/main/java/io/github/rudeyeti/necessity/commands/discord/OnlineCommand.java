package io.github.rudeyeti.necessity.commands.discord;

import io.github.rudeyeti.necessity.modules.status.Status;

public class OnlineCommand {
    protected static void execute() {
        CommandManager.textChannel.sendMessage(Status.serverOn(false).build()).queue();
    }
}
