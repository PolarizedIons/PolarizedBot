package net.polarizedions.polarizedbot.api_handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.polarizedbot.exceptions.ApiException;
import net.polarizedions.polarizedbot.util.WebHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class GithubApi {
    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/%s/releases/latest";

    @NotNull
    @Contract("_, _ -> new")
    public static Release getLatestRelease(String repo, Function<JsonObject, Boolean> artifactFilter) throws ApiException {
        JsonObject json = WebHelper.fetchJson(String.format(LATEST_RELEASE_URL, repo));

        if (json == null) {
            throw new ApiException("no_response");
        }

        if (json.get("message") != null) {
            String errMessage = json.get("message").getAsString();
            if (errMessage.equals("not found")) {
                throw new ApiException("not_found", repo);
            }

            throw new ApiException("github", errMessage);
        }

        String tag_name = json.get("tag_name").getAsString();

        String downloadName = null;
        String downloadUrl = null;
        for (JsonElement element : json.getAsJsonArray("assets")) {
            JsonObject asset = element.getAsJsonObject();
            if (artifactFilter.apply(asset)) {
                downloadName = asset.get("name").getAsString();
                downloadUrl = asset.get("browser_download_url").getAsString();
                break;
            }
        }

        if (downloadName == null) {
            throw new ApiException("asset_not_found");
        }

        return new Release(tag_name, downloadName, downloadUrl);
    }

    public static class Release {

        public final String tag;
        public final String name;
        public final String url;

        Release(String tag, String name, String url) {
            this.tag = tag;
            this.name = name;
            this.url = url;
        }
    }
}
