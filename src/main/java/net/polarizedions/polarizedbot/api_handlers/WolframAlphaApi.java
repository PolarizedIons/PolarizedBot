package net.polarizedions.polarizedbot.api_handlers;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.util.WebHelper;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WolframAlphaApi {
    private static final String API_URL = "http://api.wolframalpha.com/v2/query?input=%s&appid=%s";
    private static String API_KEY = null;

    public static boolean hasApiKey() {
        if (API_KEY == null) {
            API_KEY = Bot.instance.getGlobalConfig().wolframAlphaApi;
        }

        return !API_KEY.isEmpty();
    }

    @Nullable
    public static Map<String, List<String>> fetch(String input) {
        if (!hasApiKey()) {
            return null;
        }

        Document doc = WebHelper.fetchDom(String.format(API_URL, WebHelper.encodeURIComponent(input), API_KEY));
        if (doc == null) {
            return null;
        }

        Map<String, List<String>> data = new LinkedHashMap<>();

        NodeList pods = doc.getElementsByTagName("pod");
        for (int i = 0; i < pods.getLength(); i++) {
            Node pod = pods.item(i);
//            System.out.println("Node #" + i + ":");
            String podTitle = pod.getAttributes().getNamedItem("title").getTextContent();
//            System.out.println("   Title: " + podTitle);
            data.put(podTitle, new ArrayList<>());

            NodeList posChildren = pod.getChildNodes();
            for (int j = 0; j < posChildren.getLength(); j++) {
                Node subpod = posChildren.item(j);
                if (!subpod.getNodeName().equals("subpod")) {
                    continue;
                }

                NodeList subpodChildren = subpod.getChildNodes();
                for (int k = 0; k < subpodChildren.getLength(); k++) {
                    Node plaintextNode = subpodChildren.item(k);
                    if (!plaintextNode.getNodeName().equals("plaintext")) {
                        continue;
                    }

                    String plaintext = plaintextNode.getTextContent();
//                    System.out.println("       -> " + plaintext);
                    if (!plaintext.isEmpty()) {
                        data.get(podTitle).add(plaintext);
                    }
                }
            }
        }

        return data;
    }
}
