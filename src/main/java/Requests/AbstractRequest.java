package Requests;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

abstract class AbstractRequest implements Request {

    public static void sendStringRequest(HttpExchange request, String data, int restAPICode) throws IOException {
        request.sendResponseHeaders(restAPICode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }

    public static void sendFailedServerResponse(HttpExchange request) {
        try {
            sendStringRequest(request, "INTERNAL SERVER ERROR", 500);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception at sendFailedServerResponse");
        }
    }

    public static void sendBadRequestResponse(HttpExchange request) {
        try {
            sendStringRequest(request, "BAD REQUEST", 400);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception at sendBadRequestResponse");
        }
    }

    public static void sendOkResponse(HttpExchange request) {
        try {
            sendStringRequest(request, "OK", 200);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception at sendOkResponse");
        }
    }

    public static void sendNotFoundResponse(HttpExchange request) {
        try {
            sendStringRequest(request, "NOT FOUND", 404);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception at sendNotFoundResponse");
        }
    }

}
