package io.github.rudeyeti.necessity.modules.integration;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.okhttp3.OkHttpClient;
import github.scarsz.discordsrv.dependencies.okhttp3.Request;
import github.scarsz.discordsrv.dependencies.okhttp3.ResponseBody;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncBuildersLegacy {
    protected static int membersSize;

    protected synchronized static ResponseBody getWebsite(int pageNumber) {
        try {
            Request request = new Request.Builder()
                    .url(Config.get.buildTeamMembers + "?page=" + pageNumber)
                    .header("User-Agent", "Necessity")
                    .build();
            return new OkHttpClient().newCall(request).execute().body();
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    protected synchronized static List<Member> getWebsiteMembersList() {
        List<Member> members = new ArrayList<>();
        membersSize = 0;

        for (int i = 1; i < Necessity.lastPage + 1; i++) {
            List<String> builders = Html.getBuilders(getWebsite(i));

            for (int a = 1; a < builders.size(); a += 3) {
                Member member = Necessity.guild.getMemberByTag(builders.get(a));
                membersSize++;

                if (member != null) {
                    members.add(member);
                }
            }
        }
        return members;
    }
}
