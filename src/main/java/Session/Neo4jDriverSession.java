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

            // checking if Kevin Bacon is in the database once the connection is established for the first time.
            // if not, add him there w/ a fixed id as said in the handout.
            // if he is, ignore and proceed straight to returning the database driver reference.
            
            try (Session session = driver.session()) { //checking if id already exists

                String checkForExistingActor = "MATCH (a:Actor {actorId: $actorId}) RETURN COUNT(a) AS count";
                StatementResult result = session.run(checkForExistingActor, Values.parameters("actorId", "nm0000102"));
                if (result.hasNext()) {
                    Record record = result.next();
                    int count = record.get("count").asInt();
                    if (count != 0) {
                        return driver;
                    }
                }
    
                String createActorQuery = "CREATE (:Actor {name: $name, actorId: $actorId})";
                driver.session().run(createActorQuery, Values.parameters("name", "Kevin Bacon", "actorId", "nm0000102"));
    
    
            } catch (Exception e) {
                // Handle exceptions and log errors if needed
                e.printStackTrace();
                throw e;
            }
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
