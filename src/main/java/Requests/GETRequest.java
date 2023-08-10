package Requests;

import Session.Neo4jDriverSession;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.Record;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GETRequest extends AbstractRequest {

    private interface EndpointHandler {
        Map<String, Object> handleEndpoint(Map<String, String> requestQuery);
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
        try { //getting the query of the query and putting it into the getRequestQuery map
            String query = request.getRequestURI().getQuery();
            if (query == null) {//if the query does not contain query data then throw exception, this can be replaced with a table of content feature
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

        Map<String, Object> jsonResponse = handleAPICall.handleEndpoint(getRequestQuery);

        if (jsonResponse == null) { //if the method returns a null object then something went wrong in the method
            sendBadRequestResponse(request);
        } else {
            if (jsonResponse.isEmpty()) {
                sendNotFoundResponse(request);
                return;
            }
            try {
                sendStringRequest(request, Utils.MapToJSONBody(jsonResponse), 200);
            } catch (IOException e) {
                sendFailedServerResponse(request);
            }
        }
    }

    private static Map<String, Object> getActor(Map<String, String> requestQuery) {
        System.out.println("Called getActor");
        if (requestQuery.size() != 1 || !requestQuery.containsKey("actorId")) {//checking for invalid inputs
            return null;
        }

        String actorId = requestQuery.get("actorId");
        Driver driver = Neo4jDriverSession.getDriverInstance();
        try (Session session = driver.session()) { //checking if id already exists
            String checkForExistingActor = "MATCH (a:Actor {actorId: $actorId}) RETURN COUNT(a) AS count";
            StatementResult result = session.run(checkForExistingActor, Values.parameters("actorId", actorId));
            if (result.hasNext()) {
                Record record = result.next();
                int count = record.get("count").asInt();
                if (count == 0) {//if the count is 0 then the actor does not exist in the database
                    return new HashMap<>();
                }
            }
        }
        Map<String, Object> returnJSONQuery = new HashMap<>();
        Session queryActorSession = driver.session();
        String queryActorAttributes = "MATCH (a: Actor {actorId: $actorId}) OPTIONAL MATCH (a)-[:ACTED_IN]->(m:Movie) RETURN a.name as name, a.actorId as actorId, COLLECT(m.movieId) as movieId";
        StatementResult result = queryActorSession.run(queryActorAttributes, Values.parameters("actorId", actorId));
        if (result.hasNext()) {
            Record record = result.next();
            returnJSONQuery.put("actorId", actorId);
            returnJSONQuery.put("name", record.get("name").toString().replace("\"", "")); //the name is being returning with "" so this workaround will fix it
            returnJSONQuery.put("movies", record.get("movieId").asList(Value::asString));
        }
        return returnJSONQuery;
    }

    private static Map<String, Object> getMovie(Map<String, String> requestQuery) {
        System.out.println("Called getMovie");
        if (requestQuery.size() != 1 || !requestQuery.containsKey("movieId")) {//checking invalid inputs
            return null;
        }
        String movieId = requestQuery.get("movieId");
        Driver driver = Neo4jDriverSession.getDriverInstance();
        try (Session session = driver.session()) { //checking if id already exists
            String checkForExistingMovie = "MATCH (a:Movie {movieId: $movieId}) RETURN COUNT(a) AS count";
            StatementResult result = session.run(checkForExistingMovie, Values.parameters("movieId", movieId));
            if (result.hasNext()) {
                Record record = result.next();
                int count = record.get("count").asInt();
                if (count == 0) {//if the count is 0 then the movie does not exist in the database
                    return new HashMap<>();
                }
            }
        }
        Map<String, Object> returnJSONQuery = new HashMap<>();
        Session queryMovieSession = driver.session();
        String queryMovieAttributes = "MATCH (m: Movie {movieId: $movieId}) OPTIONAL MATCH (m)<-[:ACTED_IN]-(a:Actor) RETURN m.name as name, m.movieId as movieId, COLLECT(a.actorId) as actorId";
        StatementResult result = queryMovieSession.run(queryMovieAttributes, Values.parameters("movieId", movieId));
        if (result.hasNext()) {
            Record record = result.next();
            returnJSONQuery.put("movieId", movieId);
            returnJSONQuery.put("name", record.get("name").toString().replace("\"", "")); //the name is being returning with "" so this workaround will fix it
            returnJSONQuery.put("actors", record.get("actorId").asList(Value::asString));
        }
        return returnJSONQuery;
    }

    private static Map<String, Object> hasRelationship(Map<String, String> requestQuery) {
        System.out.println("Called hasRelationship");
        if (requestQuery.size() != 2 || !requestQuery.containsKey("movieId") || !requestQuery.containsKey("actorId")) {
            return null;
        }
        String movieId = requestQuery.get("movieId");
        String actorId = requestQuery.get("actorId");
        Driver driver = Neo4jDriverSession.getDriverInstance();
        try (Session session = driver.session()) {//check if the database contains the actor and the movie
            String checkForExistingMovie = "MATCH (a:Movie {movieId: $movieId}) RETURN COUNT(a) AS count";
            String checkForExistingActor = "MATCH (a:Actor {actorId: $actorId}) RETURN COUNT(a) AS count";
            StatementResult resultMovie = session.run(checkForExistingMovie, Values.parameters("movieId", movieId));
            Session actorSession = driver.session();
            StatementResult resultActor = actorSession.run(checkForExistingActor, Values.parameters("actorId", actorId));
            if (resultMovie.hasNext()) {//checking if movie exists
                Record record = resultMovie.next();
                int count = record.get("count").asInt();
                if (count == 0) {
                    return new HashMap<>();
                }
            }
            if (resultActor.hasNext()) {//checking if actor exists
                Record record = resultActor.next();
                int count = record.get("count").asInt();
                if (count == 0) {
                    return new HashMap<>();
                }
            }
        }

        Map<String, Object> returnJSONQuery = new HashMap<>();
        Session querySession = driver.session();
        String queryRelationship = "MATCH (a:Actor {actorId: $actorId}), (m:Movie {movieId: $movieId}) OPTIONAL MATCH (a)-[r:ACTED_IN]->(m) RETURN r IS NOT NULL as hasRelationship";
        StatementResult result = querySession.run(queryRelationship, Values.parameters("actorId", actorId, "movieId", movieId));
        if (result.hasNext()) {
            Record record = result.next();
            returnJSONQuery.put("actorId", actorId);
            returnJSONQuery.put("movieId", movieId);
            returnJSONQuery.put("hasRelationship", record.get("hasRelationship").asBoolean());
        }
        return returnJSONQuery;
    }

    private static Map<String, Object> computeBaconNumber(Map<String, String> requestQuery) {
        System.out.println("Called computeBaconNumber");

        if (!requestQuery.containsKey("actorId")) {
            return null;
        }
        String actorId = requestQuery.get("actorId");

        String baconNumberQuery = "MATCH p=shortestPath((bacon:Actor {name: 'Kevin Bacon'})-[*..6]-(actor:Actor {actorId: $actorId})) " +
                                  "RETURN length(p)/2 AS baconNumber";

        Map<String, Object> returnJSONQuery = new HashMap<>();
        try (Session session = Neo4jDriverSession.getDriverInstance().session()) {
            StatementResult result = session.run(baconNumberQuery, Values.parameters("actorId", actorId));
            if (result.hasNext()) {
                Record record = result.next();
                int baconNumber = record.get("baconNumber").asInt();
                //returnJSONQuery.put("actorId", actorId); // * uncomment this line for testing purposes *
                returnJSONQuery.put("baconNumber", baconNumber);
            }
        }
        return returnJSONQuery;
    }

    private static Map<String, Object> computeBaconPath(Map<String, String> requestQuery) {
        System.out.println("Called computeBaconPath");
        if (requestQuery.size() != 1 || !requestQuery.containsKey("actorId")) {
            return null;
        }

        String actorId = requestQuery.get("actorId");
        Map<String, Object> returnJSONQuery = new HashMap<>();
        try (Session session = Neo4jDriverSession.getDriverInstance().session()) {
            String queryBaconPath = "MATCH p=shortestPath((b:Actor {name:'Kevin Bacon'})-[*]-(a:Actor {actorId: $actorId})) RETURN nodes(p) as path";
            StatementResult result = session.run(queryBaconPath, Values.parameters("actorId", actorId));
            if (result.hasNext()) {
                List<String> path = new ArrayList<>();
                for (Node node : result.next().get("path").asList(Value::asNode)) {

                    if (node.get("actorId").asString() == "null") path.add(node.get("movieId").asString());
                    else path.add(node.get("actorId").asString());
                }
                //returnJSONQuery.put("actorId", actorId); // * uncomment this line for testing purposes *

                Collections.reverse(path);
                returnJSONQuery.put("baconPath", path);
            }
        }
        return returnJSONQuery;
    }
}
