package Requests;

import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GETRequest extends AbstractRequest {

    private interface EndpointHandler {
        Map<String, String> handleEndpoint(Map<String, String> requestQuery);
    }

    private static Map<String, GETRequest.EndpointHandler> endpointHandlers = new HashMap<>();

    public GETRequest() {
        endpointHandlers.put("/getActor", GETRequest::getActor);
        endpointHandlers.put("/getMovie", GETRequest::getMovie);
        endpointHandlers.put("/hasRelationship", GETRequest::hasRelationship);
        endpointHandlers.put("/computeBaconNumber", GETRequest::computeBaconNumber);
        endpointHandlers.put("/computeBaconPath", GETRequest::computeBaconPath);

    }

    @Override
    public void handleRequest(HttpExchange request) {
        System.out.println("Handling get request");
        Map<String, String> getRequestQuery;
        try { //getting the query of the json and putting it into the getRequestQuery map
            String query = request.getRequestURI().getQuery();
            if (query == null){//if the query does not contain json data then throw exception
                throw new UnsupportedEncodingException();
            }
            getRequestQuery = Utils.splitQuery(query);
        } catch (UnsupportedEncodingException e) {//any exceptions thrown will just send a bad request back to client
            sendBadRequestResponse(request);
            return;
        }

        String endPointFromURI = Utils.getEndpointFromPath(request);//here we will figure out which endpoint the user wants
        GETRequest.EndpointHandler handleAPICall = endpointHandlers.get(endPointFromURI);
        if (handleAPICall == null) {// if the user asked for an endpoint which did not exist then send a badRequest response
            sendBadRequestResponse(request);
            return;
        }

        Map<String, String> jsonResponse = handleAPICall.handleEndpoint(getRequestQuery);
        if (jsonResponse == null) { //if the method returns a null object then something went wrong in the method
            sendBadRequestResponse(request);
        } else {
            sendOkResponse(request);
            //figure out how to send the map as json response
        }
    }

    private static Map<String, String> getActor(Map<String, String> requestQuery) {
        System.out.println("Called getActor");
        return null;
    }

    private static Map<String, String> getMovie(Map<String, String> requestQuery) {
        System.out.println("Called getMovie");
        return null;
    }

    private static Map<String, String> hasRelationship(Map<String, String> requestQuery) {
        System.out.println("Called hasRelationship");
        return null;
    }

    private static Map<String, String> computeBaconNumber(Map<String, String> requestQuery) {
        System.out.println("Called computeBaconNumber");
        return null;
    }

    private static Map<String, String> computeBaconPath(Map<String, String> requestQuery) {
        System.out.println("Called computeBaconPath");
        return null;
    }
}
