package eu.ehri.project.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class Bundle {

    @NotNull
    private final String id;

    @NotNull
    private final Type type;

    @NotNull
    private final Map<String, Object> data;

    private Bundle(String id, Type type, Map<String,Object> data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public Bundle withId(String id) {
        return new Bundle(id, type, data);
    }

    public Bundle withData(Map<String, Object> data) {
        return new Bundle(id, type, data);
    }

    @JsonCreator
    public static Bundle create(
            @JsonProperty("id") String id,
            @JsonProperty("type") Type type,
            @JsonProperty("data") Map<String, Object> data) {
        return new Bundle(id, type, data);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    @JsonProperty("data")
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bundle bundle = (Bundle) o;

        return data.equals(bundle.data)
                && id.equals(bundle.id)
                && type == bundle.type;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }
}
