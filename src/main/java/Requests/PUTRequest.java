package Requests;

import Session.Neo4jDriverSession;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Driver;

class PUTRequest extends AbstractRequest {
    private interface EndpointHandler {
        boolean handleEndpoint(Map<String, String> requestQuery);
    }

    private static Map<String, EndpointHandler> endpointHandlers = new HashMap<>(); //map allows to add endpoints easier


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
            if (query == null){ //if the query does not contain json data then throw exception
                throw new UnsupportedEncodingException();
            }
            getRequestQuery = Utils.splitQuery(query);
        } catch (UnsupportedEncodingException e) { //any exceptions thrown will just send a bad request back to client
            sendBadRequestResponse(request);
            return;
        }


        String endPointFromURI = Utils.getEndpointFromPath(request); //here we will figure out which endpoint the user wants
        EndpointHandler handleAPICall = endpointHandlers.get(endPointFromURI);
        if (handleAPICall == null) {// if the user asked for an endpoint which did not exist then send a badRequest response
            sendBadRequestResponse(request);
            return;
        }
        if (handleAPICall.handleEndpoint(getRequestQuery)) { //otherwise call the method from the map, if the method returns false then send bad request
            sendOkResponse(request);
        } else {
            sendBadRequestResponse(request);
        }
    }

    private static boolean addActor(Map<String, String> requestQuery) {
        System.out.println("Called addActor");
        if (requestQuery.keySet().size() != 2){ //does not contain exactly 2 body parameters
            return false;
        }
        Driver driver = Neo4jDriverSession.getDriverInstance();


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
