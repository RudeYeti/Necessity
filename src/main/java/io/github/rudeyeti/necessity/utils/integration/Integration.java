package io.github.rudeyeti.necessity.utils.integration;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jsoup.nodes.Document;

import java.util.List;

public class Integration {
    public static void join(PlayerJoinEvent event) {
        String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(event.getPlayer().getUniqueId());

        if (userId != null) {
            Member member = Necessity.guild.getMemberById(userId);
            boolean hasRole = member.getRoles().contains(Necessity.builderRole);
            boolean inGroup = Plugins.getPermissions().playerInGroup(event.getPlayer(), Config.get.minecraftRoleName);

            if (hasRole && !inGroup) {
                Plugins.getPermissions().playerAddGroup(event.getPlayer(), Config.get.minecraftRoleName);
                logRoleChange(member, "promoted to", Config.get.minecraftRoleName, false);
            } else if (!hasRole && inGroup) {
                Plugins.getPermissions().playerRemoveGroup(event.getPlayer(), Config.get.minecraftRoleName);
                logRoleChange(member, "demoted from", Config.get.minecraftRoleName, false);
            }
        }
    }

    public static void initialRequest() {
        try {
            Document membersFirstPage = Config.get.legacyMode ? SyncBuildersLegacy.getWebsite(1) : SyncBuilders.getWebsite();
            String initialBuilders = Config.get.legacyMode ? membersFirstPage.select("small").text() : String.valueOf(SyncBuilders.getBuilders(membersFirstPage).size());

            if (Config.get.legacyMode) {
                try {
                    Necessity.lastPage = Integer.parseInt(membersFirstPage.select("div.pagination").select("a").last().text());
                } catch (NullPointerException error) {
                    Necessity.lastPage = 1;
                }
            }

            if (!Config.get.globalRoleChanges) {
                Necessity.initialBuildTeamMembersList = Config.get.legacyMode ? SyncBuildersLegacy.getWebsiteMembersList() : SyncBuilders.getWebsiteMembersList();
            }

            while (true) {
                Thread.sleep(1000);

                membersFirstPage = Config.get.legacyMode ? SyncBuildersLegacy.getWebsite(1) : SyncBuilders.getWebsite();
                String builders = Config.get.legacyMode ? membersFirstPage.select("small").text() : String.valueOf(SyncBuilders.getBuilders(membersFirstPage).size());

                if (initialBuilders.equals(builders)) {
                    continue;
                }

                sync();

                if (!Config.get.globalRoleChanges) {
                    Necessity.initialBuildTeamMembersList = Config.get.legacyMode ? SyncBuildersLegacy.getWebsiteMembersList() : SyncBuilders.getWebsiteMembersList();
                }

                initialBuilders = builders;

                if (Config.get.legacyMode) {
                    try {
                        Necessity.lastPage = Integer.parseInt(membersFirstPage.select("div.pagination").select("a").last().text());
                    } catch (NullPointerException error) {
                        Necessity.lastPage = 1;
                    }
                }
            }
        } catch (InterruptedException error) {
            error.printStackTrace();
        }
    }

    public synchronized static void logRoleChange(Member member, String roleChange, String roleName, boolean inGroup) {
        if (Config.get.logRoleChanges) {
            String message = "The user " + member.getUser().getAsTag() + " was " + roleChange + " " + roleName;
            if (inGroup) {
                Necessity.logger.info(message + " and " + Config.get.minecraftRoleName + ".");
            } else {
                Necessity.logger.info(message + ".");
            }
        }
    }

    public synchronized static void addRole(Member member) {
        Player player = Bukkit.getPlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId()));
        boolean hasRole = member.getRoles().contains(Necessity.builderRole);
        boolean inGroup = player != null && !Plugins.getPermissions().playerInGroup(player, Config.get.minecraftRoleName);

        if (!hasRole) {
            Necessity.guild.addRoleToMember(member, Necessity.builderRole).queue();

            if (inGroup) {
                Plugins.getPermissions().playerAddGroup(player, Config.get.minecraftRoleName);
            }

            logRoleChange(member, "promoted to", Necessity.builderRole.getName(), inGroup);
        }
    }

    public synchronized static void removeRole(Member member) {
        Player player = Bukkit.getPlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId()));
        boolean inGroup = player != null && Plugins.getPermissions().playerInGroup(player, Config.get.minecraftRoleName);
        Necessity.guild.removeRoleFromMember(member, Necessity.builderRole).queue();

        if (inGroup) {
            Plugins.getPermissions().playerRemoveGroup(player, Config.get.minecraftRoleName);
        }

        logRoleChange(member, "demoted from", Necessity.builderRole.getName(), inGroup);
    }

    public synchronized static void syncAllUsers() {
        List<Member> members = Config.get.legacyMode ? SyncBuildersLegacy.getWebsiteMembersList() : SyncBuilders.getWebsiteMembersList();
        List<Member> membersToDemote = Necessity.guild.getMembersWithRoles(Necessity.builderRole);

        for (Member member : members) {
            membersToDemote.remove(member);
            Integration.addRole(member);
        }

        for (Member member : membersToDemote) {
            Integration.removeRole(member);
        }
    }

    public synchronized static void syncUser() {
        int initialMembersSize = Config.get.legacyMode ? SyncBuildersLegacy.membersSize : SyncBuilders.membersSize;
        List<Member> members = Config.get.legacyMode ? SyncBuildersLegacy.getWebsiteMembersList() : SyncBuilders.getWebsiteMembersList();
        int membersSize = Config.get.legacyMode ? SyncBuildersLegacy.membersSize : SyncBuilders.membersSize;

        Member member;
        if (initialMembersSize - membersSize > 0) {
            Necessity.initialBuildTeamMembersList.removeAll(members);
            member = Necessity.initialBuildTeamMembersList.get(0);
            Integration.removeRole(member);
        } else {
            members.removeAll(Necessity.initialBuildTeamMembersList);
            member = members.get(0);
            Integration.addRole(member);
        }

        Necessity.lastRoleChange = member;
    }

    public synchronized static void sync() {
        if (Config.get.globalRoleChanges) {
            Integration.syncAllUsers();
        } else {
            Integration.syncUser();
        }
    }
}
