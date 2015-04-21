package eu.ehri.project;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import eu.ehri.project.resources.BackendResource;
import eu.ehri.project.health.TemplateHealthCheck;

public class BackendApplication extends Application<BackendConfiguration> {
    public static void main(String[] args) throws Exception {
        new BackendApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<BackendConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(BackendConfiguration configuration,
            Environment environment) {
        final BackendResource resource = new BackendResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }
}