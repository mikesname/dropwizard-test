package eu.ehri.project;

import eu.ehri.project.core.data.Serializer;
import eu.ehri.project.providers.ConstraintViolationExceptionMapper;
import eu.ehri.project.resources.BackendDbManager;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import eu.ehri.project.resources.BackendResource;
import eu.ehri.project.health.DatabaseHealthCheck;
import org.neo4j.graphdb.GraphDatabaseService;


public class BackendApplication extends Application<BackendConfiguration> {
    public static void main(String[] args) throws Exception {
        new BackendApplication().run(args);
    }

    @Override
    public String getName() {
        return "ehri-backend";
    }

    @Override
    public void initialize(Bootstrap<BackendConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(BackendConfiguration configuration, Environment environment) {
        final GraphDatabaseService graphDb = configuration.getGraphDatabase();
        final BackendDbManager dbManager = new BackendDbManager(graphDb);
        final BackendResource resource = new BackendResource(graphDb,
                Serializer.from(configuration.getSerializationConfig()));
        final DatabaseHealthCheck healthCheck = new DatabaseHealthCheck(graphDb);
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
        environment.jersey().register(ConstraintViolationExceptionMapper.class);
        environment.lifecycle().manage(dbManager);
    }
}