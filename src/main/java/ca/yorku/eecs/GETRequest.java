package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;

public class GETRequest extends AbstractRequest{
    @Override
    public void handleRequest(HttpExchange request) {
        System.out.println("Request GET");
    }
}
