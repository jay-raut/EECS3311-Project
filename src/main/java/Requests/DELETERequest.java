package Requests;

import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DELETERequest extends AbstractRequest {

    private interface EndpointHandler {
        boolean handleEndpoint(Map<String, String> requestQuery);
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

        if (handleAPICall.handleEndpoint(getRequestQuery)) {//otherwise call the method from the map, if the method returns false then send bad request
            sendOkResponse(request);
        } else {
            sendBadRequestResponse(request);
        }

    }

    public static boolean deleteActor(Map<String, String> requestQuery) { //arguments actorId: id
        System.out.println("called delete actor");
        return true;
    }

    public static boolean deleteMovie(Map<String, String> requestQuery) { //arguments movieId: id
        System.out.println("called delete movie");
        return true;
    }

}
