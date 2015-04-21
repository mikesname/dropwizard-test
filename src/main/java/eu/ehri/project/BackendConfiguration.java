package eu.ehri.project;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class BackendConfiguration extends Configuration {

    public GraphDatabaseService getGraphDatabase() {
        return new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(getDbPath())
                .newGraphDatabase();
    }

    @NotEmpty
    private String dbPath;

    @JsonProperty("dbPath")
    public String getDbPath() {
        return dbPath;
    }

    @JsonProperty
    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }
}