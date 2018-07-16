package io.github.polarizedions.polarizedbot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WebHelper {
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0";
    private static final Logger logger = LogManager.getLogger("WebHelper");
    private static final JsonParser parser = new JsonParser();

    public static InputStreamReader fetchUrl(String uri) {
        logger.debug("Fetching url: " + uri);
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            logger.error("Error fetching url: Malformed url!", e);
            return null;
        }

        URLConnection httpConn;
        try {
            httpConn = url.openConnection();
        } catch (IOException e) {
            logger.error("Error fetching url: IOException while opening connection!", e);
            return null;
        }
        httpConn.addRequestProperty("User-Agent", USER_AGENT);

        InputStreamReader reader;
        try {
            reader = new InputStreamReader(httpConn.getInputStream());
        } catch (IOException e) {
            logger.error("Error fetching url: Can't open stream!", e);
            return null;
        }

        return reader;
    }

    public static JsonObject fetchJson(String uri) {
        InputStreamReader reader = fetchUrl(uri);
        return reader == null ? null : parser.parse(reader).getAsJsonObject();
    }
}
