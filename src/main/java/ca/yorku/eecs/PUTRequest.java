package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class PUTRequest extends AbstractRequest {
    @Override
    public void handleRequest(HttpExchange request) {
        System.out.println("Handling put request");
        Map<String, String> getRequestQuery;
        try { //getting the query of the json and putting it into the getRequestQuery map
            getRequestQuery = Utils.splitQuery(request.getRequestURI().getQuery());
        } catch (UnsupportedEncodingException e) {
            sendFailedServerResponse(request);
            throw new RuntimeException("Internal Server Error at PUTRequest");
        }



        String requestURI = request.getRequestURI().toString();
        if (requestURI.contains("addActor")) { //checking the endpoints
            if (addActor(getRequestQuery)) {
                sendOkResponse(request);
            } else {
                sendBadRequestResponse(request);
            }
        }
        else if (requestURI.contains("addMovie")) {
            if (addMovie(getRequestQuery)) {
                sendOkResponse(request);
            } else {
                sendBadRequestResponse(request);
            }
        }
        else if (requestURI.contains("addRelationship")) {
            if (addRelationship(getRequestQuery)) {
                sendOkResponse(request);
            } else {
                sendBadRequestResponse(request);
            }
        }
        else{ //if the api caller requested a method which is not implemented
            sendBadRequestResponse(request);
        }

    }

    private boolean addActor(Map<String, String> requestQuery) {
        return true;
    }

    private boolean addMovie(Map<String, String> requestQuery) {
        return true;
    }

    private boolean addRelationship(Map<String, String> requestQuery) {
        return true;
    }
}
