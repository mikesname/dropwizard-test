package eu.ehri.project.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.ehri.project.config.serialization.FetchConfig;
import eu.ehri.project.core.Type;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mike Bryant (http://github.com/mikesname)
 */
public class TypeSerializationConfig {
    private Type type;
    private List<FetchConfig> fetchConfigList;

    @JsonCreator
    public TypeSerializationConfig(
            @NotNull @JsonProperty("type") Type type,
            @JsonProperty("fetch") List<FetchConfig> fetchConfigList) {
        this.type = type;
        this.fetchConfigList = fetchConfigList;
    }

    public Type getType() {
        return type;
    }

    public List<FetchConfig> getFetchConfigList() {
        return fetchConfigList;
    }

    @Override
    public String toString() {
        return "<" + type + ": " + fetchConfigList.toString() + ">";
    }
}
