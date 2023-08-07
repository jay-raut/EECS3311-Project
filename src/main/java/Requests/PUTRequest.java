package Requests;

import Session.Neo4jDriverSession;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Record;
import org.json.*;

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


        String getJsonBody;
        try {
            getJsonBody = Utils.getBody(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> getRequestQuery = new HashMap<>();



        try {
            JSONObject jsonBody = new JSONObject(getJsonBody);
            Iterator iterator = jsonBody.keys();
            while (iterator.hasNext()){
                Object key = iterator.next();
                getRequestQuery.put(key.toString(), jsonBody.get(key.toString()).toString());
            }
        } catch (JSONException e) { //the json body is not formatted properly
            sendBadRequestResponse(request);
            return;
        }



        String endPointFromURI = Utils.getEndpointFromPath(request); //here we will figure out which endpoint the user wants
        EndpointHandler handleAPICall = endpointHandlers.get(endPointFromURI);
        if (handleAPICall == null) {// if the user asked for an endpoint which did not exist then send a badResponse response
            sendNotFoundResponse(request);
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
        String movieId = requestQuery.get("movieId");

        Driver driver = Neo4jDriverSession.getDriverInstance();
        try (Session session = driver.session()) { //checking if id already exists
            String checkForExistingMovie = "MATCH (a:Movie {movieId: $movieId}) RETURN COUNT(a) AS count";
            StatementResult result = session.run(checkForExistingMovie, Values.parameters("movieId", movieId));
            if (result.hasNext()) {
                Record record = result.next();
                int count = record.get("count").asInt();
                if (count != 0) {
                    return false;
                }
            }

            Session newSession = driver.session();
            String createActorQuery = "CREATE (:Movie {name: $name, movieId: $movieId})";
            newSession.run(createActorQuery, Values.parameters("name", name, "movieId", movieId));


        } catch (Exception e) {
            // Handle exceptions and log errors if needed
            e.printStackTrace();
            throw e;
        }
        return true; // Movie addition was successful
    }

    private static boolean addRelationship(Map<String, String> requestQuery) {
        System.out.println("Called addRelationship");
        if (requestQuery.size() != 2 || !requestQuery.containsKey("actorId") || !requestQuery.containsKey("movieId")) {
            return false;
        }
        Driver driver = Neo4jDriverSession.getDriverInstance();
        String actorId = requestQuery.get("actorId");
        String movieId = requestQuery.get("movieId");
        try (Session session = driver.session()) {
            String checkForExistingMovie = "MATCH (a:Movie {movieId: $movieId}) RETURN COUNT(a) AS count";
            String checkForExistingActor = "MATCH (a:Actor {actorId: $actorId}) RETURN COUNT(a) AS count";
            StatementResult databaseContainsMovie = session.run(checkForExistingMovie, Values.parameters("movieId", movieId));
            if (databaseContainsMovie.hasNext()) { //checking if the database contains the movie
                Record record = databaseContainsMovie.next();
                int count = record.get("count").asInt();
                if (count != 1) {//must be 1 only
                    return false;
                }
            }
            Session newSession = driver.session();
            StatementResult databaseContainsActor = newSession.run(checkForExistingActor, Values.parameters("actorId", actorId));
            if (databaseContainsActor.hasNext()) {//checking if the database contains the actor
                Record record = databaseContainsActor.next();
                int count = record.get("count").asInt();
                if (count != 1) {//must be 1 only
                    return false;
                }
            }

            Session checkIfRelationshipExists = driver.session(); //check if the relationship already exists
            String checkRelationshipExists = "MATCH (a:Actor {actorId: $actorId}), (m: Movie {movieId: $movieId}) RETURN EXISTS ((a)-[:ACTED_IN]->(m)) AS relationshipExists";
            StatementResult result = checkIfRelationshipExists.run(checkRelationshipExists, Values.parameters("actorId", actorId, "movieId", movieId));
            if (result.hasNext()) {
                if (result.next().get("relationshipExists").asBoolean()) {
                    return false;
                }
            }

            Session connectNodesSession = driver.session(); //create the relationship
            String createRelationship = "MATCH (a:Actor {actorId: $actorId}), (m: Movie {movieId: $movieId}) CREATE (a)-[:ACTED_IN]->(m)";
            connectNodesSession.run(createRelationship, Values.parameters("actorId", actorId, "movieId", movieId));
        }
        return true;
    }
}
