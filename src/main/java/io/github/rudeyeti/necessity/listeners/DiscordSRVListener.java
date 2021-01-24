package io.github.rudeyeti.necessity.listeners;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.utils.Discord;

public class DiscordSRVListener {
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        Discord.discordReadyEvent();
    }

    @Subscribe
    public void gameChatMessagePreProcessEvent(GameChatMessagePreProcessEvent event) {
        if (Config.get.maintenance) {
            event.setCancelled(true);
        }
    }
}
