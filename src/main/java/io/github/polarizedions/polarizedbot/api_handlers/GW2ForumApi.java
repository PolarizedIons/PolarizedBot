package io.github.polarizedions.polarizedbot.api_handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.polarizedions.polarizedbot.util.WebHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GW2ForumApi {
    private static final String RELEASE_NOTES_FORUM_CATAGORY_URL = "https://en-forum.guildwars2.com/categories/game-release-notes.json";
    private static final String DISCUSSION_URL_PART = "https://en-forum.guildwars2.com/discussion/%s.json";
    private static final Logger logger = LogManager.getLogger("Gw2ForumApi");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<GW2UpdateLising> fetchUpdates() {
        logger.debug("Fetching latest release notes from forum");
        JsonObject listingsJson = WebHelper.fetchJson(RELEASE_NOTES_FORUM_CATAGORY_URL);
        if (listingsJson == null) {
            return null;
        }

        JsonArray listings = listingsJson.getAsJsonArray("Discussions");

        List<GW2UpdateLising> updates = new ArrayList<>();
        for (JsonElement element : listings) {
            JsonObject listingJson = element.getAsJsonObject();
            GW2UpdateLising listing = new GW2UpdateLising();
            listing.id = listingJson.get("DiscussionID").getAsInt();
            listing.title = listingJson.get("Name").getAsString();

            JsonElement date = listingJson.get("DateUpdated");
            if (date.isJsonNull()) {
                date = listingJson.get("DateInserted");
            }
            try {
                listing.date = dateFormat.parse(date.getAsString());
            } catch (ParseException e) {
                logger.error("Error parsing date '{}'", date.getAsString());
            }
            JsonElement lastCommentId = listingJson.get("LastCommentID");
            listing.lastComment = lastCommentId.isJsonNull() ? -1 : lastCommentId.getAsInt();
            listing.body = listingJson.get("Body").getAsString();
            listing.user = listingJson.get("FirstName").getAsString();

            updates.add(listing);
        }

        return updates;
    }

    public static List<GW2Comment> fetchComments(int postId) {
        logger.debug("Fetching comments for post {}", postId);
        JsonObject postJson = WebHelper.fetchJson(String.format(DISCUSSION_URL_PART, postId));
        if (postJson == null) {
            return null;
        }

        JsonArray postComments = postJson.getAsJsonArray("Comments");
        List<GW2Comment> comments = new ArrayList<>();
        for (JsonElement element : postComments) {
            JsonObject commentJson = element.getAsJsonObject();
            GW2Comment comment = new GW2Comment();
            comment.id = commentJson.get("CommentID").getAsInt();
            comment.body = commentJson.get("Body").getAsString();
            comment.user = commentJson.get("InsertName").getAsString();

            JsonElement date = commentJson.get("DateUpdated");
            if (date == null) {
                date = commentJson.get("DateInserted");
            }
            try {
                comment.date = dateFormat.parse(date.toString());
            } catch (ParseException e) {
                logger.error("Error parsing date '{}'", date.getAsString());
            }
        }

        return comments;
    }

    public static class GW2UpdateLising {
        public int id;
        public String title;
        public Date date;
        public int lastComment;
        public String body;
        public String user;
        private List<GW2Comment> comments;

        public List<GW2Comment> getComments() {
            if (this.comments == null) {
                this.comments = fetchComments(this.id);
            }

            return this.comments;
        }
    }

    public static class GW2Comment {
        public int id;
        public String body;
        public String user;
        public Date date;
    }
}
