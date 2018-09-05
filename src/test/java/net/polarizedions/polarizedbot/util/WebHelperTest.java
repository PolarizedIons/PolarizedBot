package net.polarizedions.polarizedbot.util;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class WebHelperTest {
    private static boolean online;
    private static boolean checked;

    private static boolean isOnline() {
        if (!checked) {
            checked = true;
            online = isReachable("httpbin.org", 80, 500);
            System.out.println("Checking if online... " + online);
        }

        return online;
    }

    // InetAddress#isReachable just flat out does not work properly
    // https://stackoverflow.com/a/34228756
    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            }
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }


    @Test
    void fetchUrl() {
        // Don't fail because we are in an no-internet environment
        assumingThat(WebHelperTest::isOnline, () -> {
            assertNotNull(WebHelper.fetchUrl("https://httpbin.org/get"), "Get request failed");
            assertNotNull(WebHelper.fetchUrl("https://httpbin.org/redirect/2"), "Redirect test failed");
            assertNull(WebHelper.fetchUrl("https://httpbin.org/status/404"), "404 test didn't return 404???");
        });
    }

    @Test
    void fetchJson() {
        // Don't fail because we are in an no-internet environment
        assumingThat(WebHelperTest::isOnline, () -> {
            JsonObject json = WebHelper.fetchJson("https://httpbin.org/get?ping=pong");
            assertNotNull(json);

            assertEquals("pong", json.getAsJsonObject("args").get("ping").getAsString());
        });
    }

    @Test
    void fetchDom() {
        // Don't fail because we are in an no-internet environment
        assumingThat(WebHelperTest::isOnline, () -> {
            Document doc = WebHelper.fetchDom("https://httpbin.org/xml");
            assertNotNull(doc);
            assertTrue(doc.getElementsByTagName("slide").getLength() > 0);
        });
    }

    @Test
    void encodeURIComponent() {
        assertEquals("canihazcheeseburger", WebHelper.encodeURIComponent("canihazcheeseburger"));
        assertEquals("I%20can't%20believe%20it!%20The%20grand%20champion!%20Standing%20HERE%2C%20next%20to%20ME.", WebHelper.encodeURIComponent("I can't believe it! The grand champion! Standing HERE, next to ME."));
        assertEquals("Don't%20%40%20me%20%23toocoolforats", WebHelper.encodeURIComponent("Don't @ me #toocoolforats"));
        assertEquals("%26uuid%3D(%7B08f8a17f-7304-4e17-9168-75c1e6d79e0b%7D)", WebHelper.encodeURIComponent("&uuid=({08f8a17f-7304-4e17-9168-75c1e6d79e0b})"));
    }
}