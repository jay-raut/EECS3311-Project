package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractRequest implements Request {
    @Override
    public void sendStringRequest(HttpExchange request, String data, int restAPICode) throws IOException {
        request.sendResponseHeaders(restAPICode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }

    public Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
