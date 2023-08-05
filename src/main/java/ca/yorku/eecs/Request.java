package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface Request {
    public void sendStringRequest(HttpExchange request, String data, int restAPICode) throws IOException;

    public void handleRequest(HttpExchange request);

}
