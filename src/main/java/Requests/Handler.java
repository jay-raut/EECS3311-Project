package Requests;

import Session.Neo4jDriverSession;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class Handler implements HttpHandler {//using façade design pattern for this one since the Request implementations are being hidden behind other classes/methods

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                new GETRequest().handleRequest(exchange);
            } else if (exchange.getRequestMethod().equals("PUT")) {
                new PUTRequest().handleRequest(exchange);
            } else if (exchange.getRequestMethod().equals("DELETE")) {
                new DELETERequest().handleRequest(exchange);
            } else if (exchange.getRequestMethod().equals("OPTIONS")) {
                new OPTIONSRequest().handleRequest(exchange);
            } else {
                AbstractRequest.sendStringRequest(exchange, "Method not implemented\n", 501);
                System.out.printf("Request type '%s' has not been implemented%n", exchange.getRequestMethod());
            }
        } catch (Exception e) {
            System.out.println("exception at handler");
            e.printStackTrace();
            AbstractRequest.sendFailedServerResponse(exchange);
        }
    }

}
