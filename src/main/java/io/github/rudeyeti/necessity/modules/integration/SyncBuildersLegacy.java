package io.github.rudeyeti.necessity.modules.integration;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncBuildersLegacy {
    protected static int membersSize;

    protected synchronized static Document getWebsite(int pageNumber) {
        try {
            return Jsoup.connect(Config.get.buildTeamMembers + "?page=" + pageNumber).userAgent("Necessity").get();
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    protected synchronized static List<Member> getWebsiteMembersList() {
        List<Member> members = new ArrayList<>();
        membersSize = 0;

        for (int i = 1; i < Necessity.lastPage + 1; i++) {
            Elements td = getWebsite(i).select("td");

            for (int a = 1; a < td.size(); a += 3) {
                String username = td.get(a).text();
                Member member = Necessity.guild.getMemberByTag(username);
                membersSize++;

                if (member != null) {
                    members.add(member);
                }
            }
        }
        return members;
    }
}
