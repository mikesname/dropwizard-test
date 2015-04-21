package eu.ehri.project.health;

import com.codahale.metrics.health.HealthCheck;
import org.neo4j.graphdb.GraphDatabaseService;

public class DatabaseHealthCheck extends HealthCheck {
    private final GraphDatabaseService graphDb;

    public DatabaseHealthCheck(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}