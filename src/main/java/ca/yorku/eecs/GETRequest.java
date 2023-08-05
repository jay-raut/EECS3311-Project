package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class GETRequest extends AbstractRequest{
    @Override
    public void handleRequest(HttpExchange request) {
        System.out.println("Request GET");
        try{
            sendStringRequest(request, "request get", 200);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
