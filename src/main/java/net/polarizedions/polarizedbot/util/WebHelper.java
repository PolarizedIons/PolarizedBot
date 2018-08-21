package net.polarizedions.polarizedbot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class WebHelper {
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0";
    private static final Logger logger = LogManager.getLogger("WebHelper");
    private static final JsonParser parser = new JsonParser();

    public static InputStream fetchUrl(String uri) {
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

        try {
            return httpConn.getInputStream();
        } catch (IOException e) {
            logger.error("Error fetching url: Can't open stream!", e);
            return null;
        }
    }

    public static JsonObject fetchJson(String uri) {
        InputStream is = fetchUrl(uri);
        return is == null ? null : parser.parse(new InputStreamReader(is)).getAsJsonObject();
    }

    public static Document fetchDom(String uri) {
        InputStream is = fetchUrl(uri);

        if (is == null) {
            return null;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        try {
            return builder.parse(is);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // From: https://stackoverflow.com/a/14424783
    public static String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                               .replaceAll("\\+", "%20")
                               .replaceAll("\\%21", "!")
                               .replaceAll("\\%27", "'")
                               .replaceAll("\\%28", "(")
                               .replaceAll("\\%29", ")")
                               .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }
}
