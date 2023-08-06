package Requests;

import Session.Neo4jDriverSession;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Record;

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
            if (query == null) { //if the query does not contain json data then throw exception
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
        if (requestQuery.size() != 2 || !requestQuery.containsKey("name") || !requestQuery.containsKey("actorId")) {
            return false; // Invalid input parameters
        }

        String name = requestQuery.get("name");
        String actorId = requestQuery.get("actorId");

        Driver driver = Neo4jDriverSession.getDriverInstance();
        try (Session session = driver.session()) { //checking if id already exists
            String checkForExistingActor = "MATCH (a:Actor {actorId: $actorId}) RETURN COUNT(a) AS count";
            StatementResult result = session.run(checkForExistingActor, Values.parameters("actorId", actorId));
            if (result.hasNext()) {
                Record record = result.next();
                int count = record.get("count").asInt();
                if (count != 0) {
                    return false;
                }
            }

            Session newSession = driver.session();
            String createActorQuery = "CREATE (:Actor {name: $name, actorId: $actorId})";
            newSession.run(createActorQuery, Values.parameters("name", name, "actorId", actorId));


        } catch (Exception e) {
            // Handle exceptions and log errors if needed
            e.printStackTrace();
            throw e;
        }
        return true; // Actor addition was successful
    }

    private static boolean addMovie(Map<String, String> requestQuery) {
        System.out.println("Called addMovie");
        if (requestQuery.size() != 2 || !requestQuery.containsKey("name") || !requestQuery.containsKey("movieId")) {
            return false; // Invalid input parameters
        }

        String name = requestQuery.get("name");
        String actorId = requestQuery.get("movieId");

        Driver driver = Neo4jDriverSession.getDriverInstance();
        try (Session session = driver.session()) { //checking if id already exists
            String checkForExistingActor = "MATCH (a:Movie {movieId: $movieId}) RETURN COUNT(a) AS count";
            StatementResult result = session.run(checkForExistingActor, Values.parameters("movieId", actorId));
            if (result.hasNext()) {
                Record record = result.next();
                int count = record.get("count").asInt();
                if (count != 0) {
                    return false;
                }
            }

            Session newSession = driver.session();
            String createActorQuery = "CREATE (:Movie {name: $name, movieId: $movieId})";
            newSession.run(createActorQuery, Values.parameters("name", name, "movieId", actorId));


        } catch (Exception e) {
            // Handle exceptions and log errors if needed
            e.printStackTrace();
            throw e;
        }
        return true; // Movie addition was successful
    }

    private static boolean addRelationship(Map<String, String> requestQuery) {
        System.out.println("Called addRelationship");
        return true;
    }
}
