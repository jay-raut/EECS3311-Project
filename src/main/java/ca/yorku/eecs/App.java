package ca.yorku.eecs;

import java.io.IOException;
import java.net.InetSocketAddress;

import Requests.Handler;
import Session.Neo4jDriverSession;
import com.sun.net.httpserver.HttpServer;

public class App 
{
    static int PORT = 8080;
    public static void main(String[] args) throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        Handler httpHandler = new Handler();
        server.createContext("/api/v1", httpHandler :: handle);	//https://localhost:8080/api/v1/...
        Neo4jDriverSession.getDriverInstance(); //initialize the driver before any API calls
        server.start();
        System.out.printf("Server started on port %d...\n", PORT);

    }
}
/**
 * this is just ayse trying to figure sht outby thinking what request will loop like for some funtions
 * feel free to fix it or roast it, i didnt change any code yet.
 * 
 * addTwoNumbers?firstNumber=1&secondNumber=2
 * 
 * PUT addActor example
 * 	{
	 	"name": "Denzel Washington",
	 	"actorId": "nm1001213"
	}
 * https://localhost:8080/api/v1/addActor?name=Denzel Washington&actorId=nm1001213
 * 
 * PUT addMovie example
 * 	{
 		"name": "Parasite",
 		"movieId": "nm7001453"
	}
 * https://localhost:8080/api/v1/addMovie?name=Parasite&movieID=nm7001453
 * 
 *
 */
