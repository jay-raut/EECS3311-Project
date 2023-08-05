package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class PUTRequest extends AbstractRequest {
    @Override
    public void handleRequest(HttpExchange request) {
        try {
            Map<String, String> getRequestQuery = splitQuery(request.getRequestURI().getQuery());
            System.out.println(getRequestQuery);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("getting request query failed");
        }
        System.out.println("Request PUT");
        try{
            sendStringRequest(request, "request put", 200);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(request.getRequestURI().toString().contains("addActor"));
    }
}
