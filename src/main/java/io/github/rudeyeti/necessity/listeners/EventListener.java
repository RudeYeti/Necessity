package io.github.rudeyeti.necessity.listeners;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.modules.integration.Integration;
import io.github.rudeyeti.necessity.modules.status.Messages;
import io.github.rudeyeti.necessity.modules.integration.Sync;
import io.github.rudeyeti.necessity.modules.status.Status;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        try {
            Integration.join(event);

            Necessity.onlinePlayers.add(event.getPlayer().getName());
            Necessity.statusChannel.editMessageById(Config.get.messageId, Status.serverOn().build()).queue();
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        try {
            Necessity.onlinePlayers.remove(event.getPlayer().getName());
            Necessity.statusChannel.editMessageById(Config.get.messageId, Status.serverOn().build()).queue();
        } catch (NullPointerException ignored) {}
    }
}
