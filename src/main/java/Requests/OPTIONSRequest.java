package Requests;

import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;

public class OPTIONSRequest extends AbstractRequest {
    private interface EndpointHandler {
        int handleEndpoint();
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
        int status = handleAPICall.handleEndpoint();
        if (status == 200) {//otherwise call the method from the map, if the method returns false then send bad request
            sendOkResponse(request);
        } else if (status == 404) {
            sendNotFoundResponse(request);
        } else {
            sendBadRequestResponse(request);
        }
    }

    private static int help (){
        System.out.println("called help");
        return 200;
    }
}
