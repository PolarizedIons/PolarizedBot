package net.polarizedions.polarizedbot.api_handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.exceptions.ApiException;
import net.polarizedions.polarizedbot.util.WebHelper;

import java.util.Comparator;
import java.util.LinkedList;

public class WolframAlphaApi {
    private static final String API_URL = "http://api.wolframalpha.com/v2/query?input=%s&appid=%s&format=plaintext&output=json";
    private static String API_KEY = null;

    public static boolean hasApiKey() {
        if (API_KEY == null) {
            API_KEY = Bot.instance.getGlobalConfig().wolframAlphaApi;
        }

        return !API_KEY.isEmpty();
    }

    public static WolframAlphaReply fetch(String input) throws ApiException {
        if (!hasApiKey()) {
            throw new ApiException("no_api_key");
        }

        JsonObject json = WebHelper.fetchJson(String.format(API_URL, WebHelper.encodeURIComponent(input), API_KEY));
        if (json == null || json.get("queryresult") == null) {
            throw new ApiException("connection");
        }

        json = json.getAsJsonObject("queryresult");

        if (json.get("parsetimedout").getAsBoolean()) {
            throw new ApiException("timed_out");
        }

        if (! json.get("success").getAsBoolean()) {
            throw new ApiException("fail");
        }

        WolframAlphaReply data = new WolframAlphaReply();

        LinkedList<Pod> pods = new LinkedList<>();
        for (JsonElement podJsonEl : json.getAsJsonArray("pods")) {
            JsonObject podJson = podJsonEl.getAsJsonObject();

            if (podJson.get("error").getAsBoolean()) {
                continue;
            }

            Pod pod = new Pod();
            pod.name = podJson.get("title").getAsString();
            pod.index = podJson.get("position").getAsInt();

            LinkedList<String> podData = new LinkedList<>();
            for (JsonElement subpodJsonEl : podJson.getAsJsonArray("subpods")) {
                JsonObject subpodJson = subpodJsonEl.getAsJsonObject();
                JsonElement textJson = subpodJson.get("plaintext");
                JsonElement imageJson = subpodJson.get("imagesource");

                String subpodText = ((textJson == null ? "" : textJson.getAsString()) + "\n" + (imageJson == null ? "" : imageJson.getAsString())).trim();
                if (subpodText.isEmpty()) {
                    continue;
                }

                podData.add(subpodText);
            }

            if (podData.size() == 0) {
                continue;
            }

            pod.data = podData;
            pods.add(pod);
        }

        pods.sort(Comparator.comparingInt(pod -> pod.index));
        data.pods = pods;

        return data;
    }

    public static class WolframAlphaReply {
        public LinkedList<Pod> pods;
    }

    public static class Pod {
        public String name;
        public int index;
        public LinkedList<String> data;
    }
}
