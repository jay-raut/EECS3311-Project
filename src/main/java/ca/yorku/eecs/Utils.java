package ca.yorku.eecs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import com.sun.net.httpserver.HttpExchange;

public class Utils {
    // use for extracting query params
    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    // one possible option for extracting JSON body as String
    public static String convert(InputStream inputStream) throws IOException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    // another option for extracting JSON body as String
    public static String getBody(HttpExchange he) throws IOException {
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);

        int b;
        StringBuilder buf = new StringBuilder();
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }

        br.close();
        isr.close();

        return buf.toString();
    }

    public static String getEndpointFromPath(HttpExchange request) {
        String requestPath = request.getRequestURI().getPath();
        requestPath = requestPath.replace("/api/v1", "");
        return requestPath;
    }

    public static Map<String, Object> JSONBodyToMap(String JSONBody) throws JSONException {
        Map<String, Object> getRequestQuery = new HashMap<>();
        JSONObject jsonBody = new JSONObject(JSONBody);
        Iterator iterator = jsonBody.keys();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            getRequestQuery.put(key.toString(), jsonBody.get(key.toString()));
        }
        return getRequestQuery;
    }
}