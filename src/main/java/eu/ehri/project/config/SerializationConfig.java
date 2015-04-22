package eu.ehri.project.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Mike Bryant (http://github.com/mikesname)
 */
public class SerializationConfig {
    private List<TypeSerializationConfig> typeSerializationConfigList;

    @JsonCreator
    public SerializationConfig(
            @JsonProperty("types") List<TypeSerializationConfig> typeSerializationConfigList) {
        this.typeSerializationConfigList = typeSerializationConfigList;
    }

    public static SerializationConfig empty() {
        return new SerializationConfig(Lists.<TypeSerializationConfig>newArrayList());
    }

    public List<TypeSerializationConfig> getTypeSerializationConfigList() {
        return typeSerializationConfigList;
    }

    public String toString() {
        return typeSerializationConfigList.toString();
    }
}
