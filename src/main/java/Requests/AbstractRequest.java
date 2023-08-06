package Requests;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractRequest implements Request {
    @Override
    public void sendStringRequest(HttpExchange request, String data, int restAPICode) throws IOException {
        request.sendResponseHeaders(restAPICode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }

    public void sendFailedServerResponse(HttpExchange request) {
        try {
            sendStringRequest(request, "INTERNAL SERVER ERROR", 500);
        } catch (IOException e) {
            System.out.println("Exception at sendFailedServerResponse");
        }
    }

    public void sendBadRequestResponse(HttpExchange request) {
        try {
            sendStringRequest(request, "BAD REQUEST", 400);
        } catch (IOException e) {
            System.out.println("Exception at sendBadRequestResponse");
        }
    }

    public void sendOkResponse(HttpExchange request) {
        try {
            sendStringRequest(request, "OK", 200);
        } catch (IOException e) {
            System.out.println("Exception at sendOkResponse");
        }
    }

    public void sendNotFoundResponse(HttpExchange request) {
        try {
            sendStringRequest(request, "NOT FOUND", 404);
        } catch (IOException e) {
            System.out.println("Exception at sendOkResponse");
        }
    }

}
