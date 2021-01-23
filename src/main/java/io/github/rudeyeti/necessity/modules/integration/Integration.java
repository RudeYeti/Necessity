package io.github.rudeyeti.necessity.modules.integration;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.modules.ModuleManager;
import org.bukkit.event.player.PlayerJoinEvent;

public class Integration {

    public static boolean isEnabled() {
        return ModuleManager.isEnabled("Integration").containsKey(true);
    }

    public static void initialize() {
        if (isEnabled()) {
            Sync.initialize();
        }
    }

    public static void join(PlayerJoinEvent event) {
        if (isEnabled()) {
            Sync.join(event);
        }
    }

    public synchronized static void sync() {
        if (isEnabled()) {
            Sync.sync();
        }
    }

    public synchronized static void syncAllUsers() {
        if (isEnabled()) {
            Sync.syncAllUsers();
        }
    }

    public synchronized static void addRole(Member member) {
        if (isEnabled()) {
            Roles.addRole(member);
        }
    }
}
