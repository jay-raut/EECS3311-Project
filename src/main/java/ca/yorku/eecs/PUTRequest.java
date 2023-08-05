package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PUTRequest extends AbstractRequest {
    @Override
    public void handleRequest(HttpExchange request) {
        System.out.println("Request PUT");
        try{
            sendStringRequest(request, "request put", 200);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
