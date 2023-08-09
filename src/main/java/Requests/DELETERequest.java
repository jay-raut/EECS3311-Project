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


        return 200;
    }

    private static int deleteMovie(Map<String, String> requestQuery) { //arguments movieId: id
        System.out.println("called delete movie");
        return 200;
    }

}