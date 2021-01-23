package io.github.rudeyeti.necessity.listeners;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import io.github.rudeyeti.necessity.utils.Discord;

public class DiscordSRVListener {
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        Discord.discordReadyEvent();
    }
}
