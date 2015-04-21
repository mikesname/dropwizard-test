package eu.ehri.project.resources;


import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import eu.ehri.project.core.Document;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/doc")
@Produces(MediaType.APPLICATION_JSON)
public class BackendResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public BackendResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Document sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.or(defaultName));
        return new Document(counter.incrementAndGet(), value);
    }
}