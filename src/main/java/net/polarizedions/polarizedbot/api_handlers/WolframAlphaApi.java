package net.polarizedions.polarizedbot.api_handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.exceptions.ApiException;
import net.polarizedions.polarizedbot.util.WebHelper;

import java.util.Collections;
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


        WolframAlphaReply data = new WolframAlphaReply();
        data.error = "";

        LinkedList<Pod> pods = new LinkedList<>();
        JsonElement podsObject = json.get("pods");
        if (podsObject == null) {
            data.error = "no_data";
        }
        else {
            for (JsonElement podJsonEl : podsObject.getAsJsonArray()) {
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

                    String subpodText = ( ( textJson == null ? "" : textJson.getAsString() ) + "\n" + ( imageJson == null ? "" : imageJson.getAsString() ) ).trim();
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

            // Needs to be checked after parsing, because some pods's text repr are empty
            if (pods.size() == 0) {
                data.error = "no_data";
            }
        }

        pods.sort(Comparator.comparingInt(pod -> pod.index));
        data.pods = pods;

        // Wolfram... WHY can this be two types?!
        JsonElement didyoumeansJson = json.get("didyoumeans");
        data.didYouMeans = new LinkedList<>();
        if (didyoumeansJson != null) {
            if (didyoumeansJson.isJsonArray()) {
                for (JsonElement didyoumeanElement : didyoumeansJson.getAsJsonArray()) {
                    JsonObject didyoumeanObj = didyoumeanElement.getAsJsonObject();
                    DidYouMean dym = new DidYouMean();
                    dym.chance = didyoumeanObj.get("score").getAsDouble() * 100;
                    dym.value = didyoumeanObj.get("val").getAsString();
                    data.didYouMeans.add(dym);
                }
            }
            else if (didyoumeansJson.isJsonObject()) {
                JsonObject didyoumeanObj = didyoumeansJson.getAsJsonObject();
                DidYouMean dym = new DidYouMean();
                dym.chance = didyoumeanObj.get("score").getAsDouble() * 100;
                dym.value = didyoumeanObj.get("val").getAsString();
                data.didYouMeans.add(dym);
            }

            data.didYouMeans.sort(Collections.reverseOrder(Comparator.comparingDouble(dym -> dym.chance)));
        }

        // Needs to be checked *after* checking for no data, because that error is more specific
        if (! json.get("success").getAsBoolean() && data.error.isEmpty()) {
            data.error = "fail";
        }

        return data;
    }

    public static class WolframAlphaReply {
        public String error;
        public LinkedList<Pod> pods;
        public LinkedList<DidYouMean> didYouMeans;
    }

    public static class Pod {
        public String name;
        public int index;
        public LinkedList<String> data;
    }

    public static class DidYouMean {
        public String value;
        public double chance;
    }
}
