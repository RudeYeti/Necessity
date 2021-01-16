package io.github.rudeyeti.necessity;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.commands.minecraft.NecessityCommand;
import io.github.rudeyeti.necessity.commands.minecraft.ServerActivityCommand;
import io.github.rudeyeti.necessity.listeners.DiscordSRVListener;
import io.github.rudeyeti.necessity.listeners.EventListener;
import io.github.rudeyeti.necessity.listeners.JDAListener;
import io.github.rudeyeti.necessity.modules.ModuleManager;
import io.github.rudeyeti.necessity.modules.status.Status;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class Necessity extends JavaPlugin {

    public static Plugin plugin;
    public static Server server;
    public static Logger logger;
    public static Guild guild;
    public static Role builderRole;
    public static List<Member> initialBuildTeamMembersList;
    public static Member lastRoleChange;
    public static int lastPage;
    public static TextChannel statusChannel;
    public static List<String> onlinePlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        plugin = getPlugin(this.getClass());
        server = plugin.getServer();
        logger = this.getLogger();
        Config.config = plugin.getConfig();
        plugin.saveDefaultConfig();

        if (Config.validateConfig(true)) {
            Config.updateConfig();
        } else {
            server.getPluginManager().disablePlugin(plugin);
            return;
        }

        ModuleManager.initialize();

        if (Config.get.whitelist && !server.hasWhitelist()) {
            server.setWhitelist(true);
        }

        if (Config.get.status) {
            // Should almost always be none, unless the plugin has somehow been reloaded.
            server.getOnlinePlayers().forEach((player) -> {
                onlinePlayers.add(player.getName());
            });
        }

        server.getPluginManager().registerEvents(new EventListener(), this);
        this.getCommand("necessity").setExecutor(new NecessityCommand());
        this.getCommand("serveractivity").setExecutor(new ServerActivityCommand());

        DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGES);
        DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MEMBERS);
        DiscordSRV.api.subscribe(new DiscordSRVListener());
    }

    @Override
    public void onDisable() {
        try {
            if (Config.get.status) {
                statusChannel.editMessageById(Config.get.messageId, Status.serverOff().build()).complete();
            }

            DiscordUtil.getJda().removeEventListener(new JDAListener());
            DiscordSRV.api.unsubscribe(new DiscordSRVListener());
        } catch (NullPointerException ignored) {}
    }
}
