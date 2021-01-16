package io.github.rudeyeti.necessity.modules.integration;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Roles {
    protected synchronized static void addRole(Member member) {
        Player player = Bukkit.getPlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId()));
        boolean hasRole = member.getRoles().contains(Necessity.builderRole);
        boolean inGroup = player != null && !Plugins.getVault().playerInGroup(player, Config.get.minecraftRoleName);

        if (!hasRole) {
            Necessity.guild.addRoleToMember(member, Necessity.builderRole).queue();

            if (inGroup) {
                Plugins.getVault().playerAddGroup(player, Config.get.minecraftRoleName);
            }

            logRoleChange(member, "promoted to", Necessity.builderRole.getName(), inGroup);
        }
    }

    protected synchronized static void removeRole(Member member) {
        Player player = Bukkit.getPlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId()));
        boolean inGroup = player != null && Plugins.getVault().playerInGroup(player, Config.get.minecraftRoleName);
        Necessity.guild.removeRoleFromMember(member, Necessity.builderRole).queue();

        if (inGroup) {
            Plugins.getVault().playerRemoveGroup(player, Config.get.minecraftRoleName);
        }

        logRoleChange(member, "demoted from", Necessity.builderRole.getName(), inGroup);
    }

    protected synchronized static void logRoleChange(Member member, String roleChange, String roleName, boolean inGroup) {
        if (Config.get.logRoleChanges) {
            String message = "The user " + member.getUser().getAsTag() + " was " + roleChange + " " + roleName;
            if (inGroup) {
                Necessity.logger.info(message + " and " + Config.get.minecraftRoleName + ".");
            } else {
                Necessity.logger.info(message + ".");
            }
        }
    }
}
