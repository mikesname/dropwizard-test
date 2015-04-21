package eu.ehri.project.resources;

import io.dropwizard.lifecycle.Managed;
import org.neo4j.graphdb.GraphDatabaseService;

public class BackendDbManager implements Managed {

    private final GraphDatabaseService graphDb;

    public BackendDbManager(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    @Override
    public void start() throws Exception {
        // DB already up...
    }

    @Override
    public void stop() throws Exception {
        graphDb.shutdown();
    }
}
