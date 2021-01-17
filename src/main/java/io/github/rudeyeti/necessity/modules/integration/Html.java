package io.github.rudeyeti.necessity.modules.integration;

import github.scarsz.discordsrv.dependencies.okhttp3.ResponseBody;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html {
    protected static List<String> getBuilders(ResponseBody html) {
        try {
            List<String> builders = new ArrayList<>();
            Pattern pattern = Pattern.compile("<td>(.+)</td>");
            Matcher matcher = pattern.matcher(html.string());

            while (matcher.find()) {
                builders.add(matcher.group(1));
            }

            return builders;
        } catch (IOException error) {
            error.printStackTrace();
        }
        return null;
    }

    protected static String getBuilderCount(ResponseBody html) {
        try {
            if (Config.get.legacyMode) {
                Pattern pattern = Pattern.compile("<small>\\((\\d+)\\)</small>");
                Matcher matcher = pattern.matcher(html.string());

                if (matcher.find()) {
                    return matcher.group(1);
                }
            } else {
                return String.valueOf(SyncBuilders.getBuilders(html).size());
            }
        } catch (IOException error) {
            error.printStackTrace();
        }
        return null;
    }

    protected static void setLastPage(ResponseBody html) {
        if (Config.get.legacyMode) {
            try {
                Pattern pattern = Pattern.compile("<a rel=\"noindex\" href=\"\\?page=\\d+&amp;#members\">(\\d+)</a>");
                Matcher matcher = pattern.matcher(html.string());

                if (matcher.find()) {
                    Necessity.lastPage = Integer.parseInt(matcher.group(1));
                }
            } catch (NullPointerException error) {
                Necessity.lastPage = 1;
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }
}
