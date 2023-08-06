package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PUTRequest extends AbstractRequest {
    private interface EndpointHandler {
        boolean handleEndpoint(Map<String, String> requestQuery);
    }

    private static Map<String, EndpointHandler> endpointHandlers = new HashMap<>();


    public PUTRequest() {
        endpointHandlers.put("/addActor", PUTRequest::addActor);
        endpointHandlers.put("/addMovie", PUTRequest::addMovie);
        endpointHandlers.put("/addRelationship", PUTRequest::addRelationship);

    }

    @Override
    public void handleRequest(HttpExchange request) {
        System.out.println("Handling put request");
        Map<String, String> getRequestQuery;
        try { //getting the query of the json and putting it into the getRequestQuery map
            String query = request.getRequestURI().getQuery();
            if (query == null){
                throw new UnsupportedEncodingException();
            }
            getRequestQuery = Utils.splitQuery(query);
        } catch (UnsupportedEncodingException e) {
            sendBadRequestResponse(request);
            return;
        }


        String endPointFromURI = Utils.getEndpointFromPath(request);
        EndpointHandler handleAPICall = endpointHandlers.get(endPointFromURI);
        if (handleAPICall == null) {
            sendBadRequestResponse(request);
            return;
        }
        if (handleAPICall.handleEndpoint(getRequestQuery)) {
            sendOkResponse(request);
        } else {
            sendBadRequestResponse(request);
        }
    }

    private static boolean addActor(Map<String, String> requestQuery) {
        System.out.println("Called addActor");
        return true;
    }

    private static boolean addMovie(Map<String, String> requestQuery) {
        System.out.println("Called addMovie");
        return true;
    }

    private static boolean addRelationship(Map<String, String> requestQuery) {
        System.out.println("Called addRelationship");
        return true;
    }
}
