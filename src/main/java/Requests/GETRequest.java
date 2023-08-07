package Requests;

import Session.Neo4jDriverSession;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Record;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
        try { //getting the query of the json and putting it into the getRequestQuery map
            String query = request.getRequestURI().getQuery();
            if (query == null) {//if the query does not contain json data then throw exception
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
                    return null;
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
        return null;
    }

    private static Map<String, Object> hasRelationship(Map<String, String> requestQuery) {
        System.out.println("Called hasRelationship");
        return null;
    }

    private static Map<String, Object> computeBaconNumber(Map<String, String> requestQuery) {
        System.out.println("Called computeBaconNumber");
        return null;
    }

    private static Map<String, Object> computeBaconPath(Map<String, String> requestQuery) {
        System.out.println("Called computeBaconPath");
        return null;
    }
}
