package ca.yorku.eecs;

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
            } else {
                sendString(exchange, "Method not implemented\n", 501);
                System.out.printf("Request type '%s' has not been implemented%n", exchange.getRequestMethod());
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendString(exchange, "Internal Server Error\n", 500);
        }
    }

    @Deprecated
    private void sendString(HttpExchange request, String data, int restCode)
            throws IOException { //will fix later
        request.sendResponseHeaders(restCode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }
}
