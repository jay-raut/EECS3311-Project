package Requests;

import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
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
            sendBadRequestResponse(request);
        }


    }

    private static Map<String, Object> help (){
        System.out.println("called help");
        return new HashMap<>();
    }
}
