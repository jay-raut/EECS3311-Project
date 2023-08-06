package Session;

import org.neo4j.driver.v1.*;

public final class Neo4jDriverSession {//using singleton design pattern to use the same driver instance
    private static final String uri = "bolt://localhost:7687";
    private static final String userName = "neo4j";
    private static final String passWord = "12345678";
    private static Driver driver = null;

    private Neo4jDriverSession() {//using singleton design pattern

    }

    public static Driver getDriverInstance() { //to instantiate do Driver driver = Neo4jDriverSession.getDriverInstance()
        if (driver == null) {
            Config config = Config.builder().withoutEncryption().build();
            driver = GraphDatabase.driver(uri, AuthTokens.basic(userName, passWord), config);
        }
        return driver;
    }

    public static void close(){ //use this method to close the driver session
        if (driver == null){
            throw new IllegalStateException("Driver has not been instantiated");
        }
        driver.close();
        driver = null; //setting back to null to ensure the same driver instance is always used
    }
}