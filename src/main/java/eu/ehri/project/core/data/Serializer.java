package eu.ehri.project.core.data;

import com.google.common.collect.Maps;
import eu.ehri.project.config.SerializationConfig;
import eu.ehri.project.core.Bundle;
import eu.ehri.project.core.Type;
import org.neo4j.graphdb.Node;

import java.util.Map;

/**
 * @author Mike Bryant (http://github.com/mikesname)
 */
public class Serializer {

    public static final String ID_KEY = "_id";

    private final SerializationConfig config;

    public Serializer(SerializationConfig config) {
        this.config = config;
    }

    public static Serializer from(SerializationConfig config) {
        return new Serializer(config);
    }

    public static Serializer empty() {
        return new Serializer(SerializationConfig.empty());
    }

    public Bundle serialize(Node node) {
        String id = (String) node.getProperty(ID_KEY);
        Type type = Type.valueOf(node.getLabels().iterator().next().name());
        Map<String, Object> data = Maps.newHashMap();
        for (String key : node.getPropertyKeys()) {
            if (!key.equals(ID_KEY)) {
                data.put(key, node.getProperty(key));
            }
        }
        return Bundle.create(id, type, data);
    }


    public SerializationConfig getConfig() {
        return config;
    }
}
