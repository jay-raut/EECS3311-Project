package Requests;

import Session.Neo4jDriverSession;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Values;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DELETERequest extends AbstractRequest {

    private interface EndpointHandler {
        int handleEndpoint(Map<String, String> requestQuery);
    }

    private static Map<String, DELETERequest.EndpointHandler> endpointHandlers = new HashMap<>(); //map allows to add endpoints easier

    public DELETERequest() {
        endpointHandlers.put("/deleteActor", DELETERequest::deleteActor);
        endpointHandlers.put("/deleteMovie", DELETERequest::deleteMovie);
        endpointHandlers.put("/deleteRelationship", DELETERequest::deleteRelationship);


    }

    @Override
    public void handleRequest(HttpExchange request) {
        System.out.println("Handling delete request");
        Map<String, String> getRequestQuery;
        try { //getting the query of the query and putting it into the getRequestQuery map
            String query = request.getRequestURI().getQuery();
            if (query == null) {//if the query does not contain query data then throw exception
                throw new UnsupportedEncodingException();
            }
            getRequestQuery = Utils.splitQuery(query);
        } catch (UnsupportedEncodingException e) {//any exceptions thrown will just send a bad request back to client
            sendBadRequestResponse(request);
            return;
        }

        String endPointFromURI = Utils.getEndpointFromPath(request);
        DELETERequest.EndpointHandler handleAPICall = endpointHandlers.get(endPointFromURI);
        if (handleAPICall == null) {//if user asked for endPoint which does not exist just send bad response
            sendBadRequestResponse(request);
            return;
        }

        int status = handleAPICall.handleEndpoint(getRequestQuery);
        if (status == 200) {//otherwise call the method from the map, if the method returns false then send bad request
            sendOkResponse(request);
        } else if (status == 404) {
            sendNotFoundResponse(request);
        } else {
            sendBadRequestResponse(request);
        }
    }

    private static int deleteActor(Map<String, String> requestQuery) { //arguments actorId: id
        System.out.println("called delete actor");
        if (requestQuery.size() != 1 || !requestQuery.containsKey("actorId")) {
            return 400;
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
                    return 404;
                }
            }
        }

        Session deleteActorSession = driver.session(); //deleting actor
        String deleteActorCommand = "MATCH (a:Actor {actorId: $actorId}) DETACH DELETE a";
        deleteActorSession.run(deleteActorCommand, Values.parameters("actorId", actorId));
        return 200;
    }

    private static int deleteMovie(Map<String, String> requestQuery) { //arguments movieId: id
        if (requestQuery.size() != 1 || !requestQuery.containsKey("movieId")) {
            return 400;
        }

        String movieId = requestQuery.get("movieId");
        Driver driver = Neo4jDriverSession.getDriverInstance();
        try (Session session = driver.session()) { //checking if id already exists
            String checkForExistingMovie = "MATCH (m:Movie {movieId: $movieId}) RETURN COUNT(m) AS count";
            StatementResult result = session.run(checkForExistingMovie, Values.parameters("movieId", movieId));
            if (result.hasNext()) {
                Record record = result.next();
                int count = record.get("count").asInt();
                if (count == 0) {//if the count is 0 then the actor does not exist in the database
                    return 404;
                }
            }
        }

        Session deleteMovieSession = driver.session(); //deleting actor
        String deleteMovieCommand = "MATCH (m:Movie {movieId: $movieId}) DETACH DELETE m";
        deleteMovieSession.run(deleteMovieCommand, Values.parameters("movieId", movieId));
        return 200;
    }

    private static int deleteRelationship(Map<String, String> requestQuery) {
        System.out.println("called deleteRelationship");
        if (requestQuery.size() != 2 || !requestQuery.containsKey("movieId") || !requestQuery.containsKey("actorId")) {
            return 400;
        }
        String movieId = requestQuery.get("movieId");
        String actorId = requestQuery.get("actorId");
        Driver driver = Neo4jDriverSession.getDriverInstance();
        try (Session session = driver.session()) {
            String checkForExistingMovie = "MATCH (m:Movie {movieId: $movieId}) RETURN COUNT(m) AS count";
            StatementResult resultMovie = session.run(checkForExistingMovie, Values.parameters("movieId", movieId));
            if (resultMovie.hasNext()) {
                Record record = resultMovie.next();
                int count = record.get("count").asInt();
                if (count == 0) {//if the count is 0 then the actor does not exist in the database
                    return 404;
                }
            }
            Session actorSession = driver.session();
            String checkForExistingActor = "MATCH (a:Actor {actorId: $actorId}) RETURN COUNT(a) AS count";
            StatementResult resultActor = actorSession.run(checkForExistingActor, Values.parameters("actorId", actorId));
            if (resultActor.hasNext()) {
                Record record = resultActor.next();
                int count = record.get("count").asInt();
                if (count == 0) {//if the count is 0 then the actor does not exist in the database
                    return 404;
                }
            }

            Session checkIfRelationshipExists = driver.session(); //check if the relationship already exists
            String checkRelationshipExists = "MATCH (a:Actor {actorId: $actorId}), (m: Movie {movieId: $movieId}) RETURN EXISTS ((a)-[:ACTED_IN]->(m)) AS relationshipExists";
            StatementResult result = checkIfRelationshipExists.run(checkRelationshipExists, Values.parameters("actorId", actorId, "movieId", movieId));
            if (result.hasNext()) {
                if (!result.next().get("relationshipExists").asBoolean()) { //if the relationship doesn't exist
                    return 404;
                }
            }

            Session DeleteNodesSession = driver.session(); //create the relationship
            String DeleteRelationship = "MATCH (a:Actor {actorId: $actorId})-[r:ACTED_IN]->(m:Movie {movieId: $movieId}) DELETE r";
            DeleteNodesSession.run(DeleteRelationship, Values.parameters("actorId", actorId, "movieId", movieId));
        }
        return 200;
    }

}
