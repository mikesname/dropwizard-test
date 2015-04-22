package eu.ehri.project;

import eu.ehri.project.config.SerializationConfig;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BackendConfiguration extends Configuration {

    @Valid
    @NotNull
    private SerializationConfig serializationConfig;

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

    @JsonProperty("serializationConfig")
    public SerializationConfig getSerializationConfig() {
        return serializationConfig;
    }

    @JsonProperty("serializationConfig")
    public void setSerializationConfig(SerializationConfig serializationConfig) {
        this.serializationConfig = serializationConfig;
    }
}