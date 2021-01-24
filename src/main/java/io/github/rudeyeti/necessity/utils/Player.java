package io.github.rudeyeti.necessity.utils;

import github.scarsz.discordsrv.dependencies.okhttp3.OkHttpClient;
import github.scarsz.discordsrv.dependencies.okhttp3.Request;
import github.scarsz.discordsrv.dependencies.okhttp3.Response;

import java.io.IOException;
import java.net.URL;

import java.util.UUID;

public class Player {
    public static boolean exists(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            Request request = new Request.Builder().url(url).header("User-Agent", "Necessity").build();
            Response response = new OkHttpClient().newBuilder().build().newCall(request).execute();

            if (response.isSuccessful() && response.body().contentLength() > 0) {
                response.close();
                return true;
            } else {
                response.close();
                return false;
            }
        } catch (IOException error) {
            return false;
        }
    }

    public static boolean isUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException error) {
            return false;
        }
    }
}
