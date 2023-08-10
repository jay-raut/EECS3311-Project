package Requests;

import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OPTIONSRequest extends AbstractRequest {
    private interface EndpointHandler {
        Map<String, Object> handleEndpoint();
    }

    private static Map<String, OPTIONSRequest.EndpointHandler> endpointHandlers = new HashMap<>(); //map allows to add endpoints easier

    public OPTIONSRequest() {
        endpointHandlers.put("/help", OPTIONSRequest::help);
    }

    @Override
    public void handleRequest(HttpExchange request) {
        System.out.println("handling options request");
        String endPointFromURI = Utils.getEndpointFromPath(request);
        OPTIONSRequest.EndpointHandler handleAPICall = endpointHandlers.get(endPointFromURI);
        if (handleAPICall == null) {//if user asked for endPoint which does not exist just send bad response
            sendBadRequestResponse(request);
            return;
        }
        Map<String, Object> returnedHelp = handleAPICall.handleEndpoint(); //get returned json object
        if (returnedHelp == null){
            sendBadRequestResponse(request); //if something failed (which it shouldn't since we are not querying the db) send a bad request response
            return;
        }
        try {//if the JSON conversion fails (which it shouldn't but try catch statement just in case)
            sendStringRequest(request, Utils.MapToJSONBody(returnedHelp), 200); //returns the json data from map
        } catch (IOException e) {
            sendFailedServerResponse(request);
        }


    }

    private static Map<String, Object> help (){
        System.out.println("called help");
        Map<String, Object> returnAvailableCommands = new LinkedHashMap<>();
        returnAvailableCommands.put("/addActor (PUT)", "Adds a actor into the database using JSON body. Takes two keys and their respective values: name:actor_name, actorId:actor_id");
        returnAvailableCommands.put("/addMovie (PUT)", "Adds a movie into the database using JSON body. Takes two keys and their respective values: name:movie_name, movieId:movie_id");
        returnAvailableCommands.put("/addRelationship (PUT)", "Creates a relationship between a actor node and a movie node using JSON body. Takes two keys and values: actorId:actor_id, movieId:movie_id");
        returnAvailableCommands.put("/getActor (GET)", "Returns a JSON response about a actor's name, id and relationships with movies using Params. actorId:actor_id");
        returnAvailableCommands.put("/getMovie (GET)", "Returns a JSON response about a movie's name, id and relationships with movies using Params. movieId:movie_id");
        returnAvailableCommands.put("/hasRelationship(GET)", "Returns a JSON response to check the relationship between a actor and movie using Params. actorId:actor_id, movieId:movie_id");
        returnAvailableCommands.put("/computeBaconNumber (GET)", "Returns a JSON response about KevinBacon's bacon number using Params. actorId:nm0000102");
        returnAvailableCommands.put("/computeBaconPath (GET)", " returns the shortest Bacon Path in order from the actor given to Kevin Bacon using Params. actorId:nm0000102");
        returnAvailableCommands.put("/deleteActor (DELETE)", "(ADDED FEATURE) Removes a actor node from the database including its relationships using Params. actorId:actor_id");
        returnAvailableCommands.put("/deleteMovie (DELETE)", "(ADDED FEATURE) Removes a movie node from the database including its relationships using Params. movieId:movie_id");
        return returnAvailableCommands;
    }
}
