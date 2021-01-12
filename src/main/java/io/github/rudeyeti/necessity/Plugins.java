package io.github.rudeyeti.necessity;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Plugins {
    public static CoreProtectAPI getCoreProtect() {
        Plugin plugin = Necessity.server.getPluginManager().getPlugin("CoreProtect");

        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();

        if (!CoreProtect.isEnabled()) {
            return null;
        }

        if (CoreProtect.APIVersion() < 4) {
            return null;
        }

        return CoreProtect;
    }

    public static Permission getPermissions() {
        RegisteredServiceProvider<Permission> provider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);

        return provider.getProvider();
    }

    public static Plugin getWorldEdit() {
        Plugin plugin = Necessity.server.getPluginManager().getPlugin("WorldEdit");

        if (!plugin.isEnabled()) {
            return null;
        }

        return plugin;
    }
}
