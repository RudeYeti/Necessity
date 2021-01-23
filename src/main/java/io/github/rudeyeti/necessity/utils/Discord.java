package io.github.rudeyeti.necessity.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.commons.lang3.ArrayUtils;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import io.github.rudeyeti.necessity.modules.ModuleManager;

public class Discord {
    public static void discordReadyEvent() {
        try {
            DiscordUtil.getJda().addEventListener(Control.jdaListener);
            Necessity.guild = DiscordSRV.getPlugin().getMainGuild();
            Necessity.builderRole = Necessity.guild.getRoleById(Config.get.discordRoleId);

            if (Control.isEnabled) {
                if (Necessity.builderRole == null) {
                    Necessity.logger.severe(String.format(Config.getMessage, "discord-role-id", "a real role."));
                    Control.disable(true);
                    return;
                } else if (!ArrayUtils.contains(Plugins.getVault().getGroups(), Config.get.minecraftRoleName)) {
                    Necessity.logger.severe(String.format(Config.getMessage, "minecraft-role-name", "a real group."));
                    Control.disable(true);
                    return;
                }
            }

            ModuleManager.initialize(false);
        } catch (NullPointerException error) {
            if (Control.isEnabled && Necessity.guild == null) {
                Necessity.logger.severe("The Discord Bot must be in a Discord Server.");
                Control.disable(true);
            }
        }

    }
}
