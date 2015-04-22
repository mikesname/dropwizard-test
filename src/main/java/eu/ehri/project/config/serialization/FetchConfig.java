package eu.ehri.project.config.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Mike Bryant (http://github.com/mikesname)
 */
public class FetchConfig {
    private String name;
    private String relName;

    @JsonCreator
    public FetchConfig(
            @NotNull @JsonProperty("name") String name,
            @NotNull @JsonProperty("relName") String relName) {
        this.name = name;
        this.relName = relName;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getRelName() {
        return relName;
    }

    @Override
    public String toString() {
        return "[" + name + " -> " + relName + "]";
    }
}
