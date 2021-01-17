package io.github.rudeyeti.necessity.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.commands.minecraft.NecessityCommand;
import io.github.rudeyeti.necessity.commands.minecraft.ServerActivityCommand;
import io.github.rudeyeti.necessity.listeners.DiscordSRVListener;
import io.github.rudeyeti.necessity.listeners.EventListener;
import io.github.rudeyeti.necessity.listeners.JDAListener;
import io.github.rudeyeti.necessity.modules.ModuleManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Control {
    public static boolean isEnabled = true;

    public static void enable() {
        try {
            isEnabled = true;

            if (Config.validateConfig(true)) {
                Config.updateConfig();
            } else {
                disable();
                return;
            }

            ModuleManager.initialize();

            if (Config.get.whitelist && !Necessity.server.hasWhitelist()) {
                Necessity.server.setWhitelist(true);
            }

            if (Config.get.status) {
                // Should almost always be none, unless the plugin has somehow been reloaded.
                Necessity.server.getOnlinePlayers().forEach((player) -> {
                    Necessity.onlinePlayers.add(player.getName());
                });
            }

            Necessity.server.getPluginManager().registerEvents(new EventListener(), Necessity.plugin);
            Necessity.server.getPluginCommand("necessity").setExecutor(new NecessityCommand());
            Necessity.server.getPluginCommand("serveractivity").setExecutor(new ServerActivityCommand());

            DiscordSRV.api.subscribe(new DiscordSRVListener());

            if (DiscordSRV.isReady) {
                DiscordSRV.api.callEvent(new DiscordReadyEvent());
            }
        } catch (NullPointerException ignored) {}
    }

    public static void disable() {
        try {
            isEnabled = false;

            Necessity.logger.severe(ChatColor.RED + "The plugin will be temporarily disabled until the configuration is changed.");
            Necessity.logger.severe(ChatColor.RED + "Once the values have been modified, reload the plugin with: /necessity reload");

            Config.updateConfig();
            Necessity.server.getPluginCommand("necessity").setExecutor(new NecessityCommand());

            Field commandMapField = Necessity.server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Necessity.server);

            commandMap.getCommand("serveractivity").unregister(commandMap);
            Necessity.server.getPluginCommand("serveractivity").setExecutor(null);

            PlayerJoinEvent.getHandlerList().unregister(Necessity.plugin);
            PlayerQuitEvent.getHandlerList().unregister(Necessity.plugin);

            DiscordUtil.getJda().removeEventListener(new JDAListener());
            DiscordSRV.api.unsubscribe(new DiscordSRVListener());
        } catch (NullPointerException ignored) {
        } catch (NoSuchFieldException | IllegalAccessException error) {
            error.printStackTrace();
        }
    }
}
