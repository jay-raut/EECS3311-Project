package Requests;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

interface Request {
    public static void sendStringRequest(HttpExchange request, String data, int restAPICode) throws IOException {
        request.sendResponseHeaders(restAPICode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }

    public void handleRequest(HttpExchange request);

}
