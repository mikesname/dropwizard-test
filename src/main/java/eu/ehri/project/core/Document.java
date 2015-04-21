package eu.ehri.project.core;

/**
 * @author Mike Bryant (http://github.com/mikesname)
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class Document {
    private long id;

    @Length(max = 3)
    private String content;

    public Document() {
        // Jackson deserialization
    }

    public Document(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }
}