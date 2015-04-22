package eu.ehri.project.resources;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterators;
import eu.ehri.project.core.Bundle;
import eu.ehri.project.core.Type;
import eu.ehri.project.core.data.Serializer;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.Schema;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


@Path("/ehri")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BackendResource {

    private static final JsonFactory jsonFactory = new JsonFactory();
    private static final ObjectMapper mapper = new ObjectMapper();

    private final GraphDatabaseService graphDb;
    private final Serializer serializer;

    @Context
    private HttpServletResponse response;

    public BackendResource(GraphDatabaseService graphDb, Serializer serializer) {
        this.graphDb = graphDb;
        this.serializer = serializer;
    }

    @POST
    @Path("schema")
    public Response createSchema() {
        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = graphDb.schema();
            for (Type t : Type.values()) {
                schema.constraintFor(DynamicLabel.label(t.name()))
                        .assertPropertyIsUnique(Serializer.ID_KEY)
                        .create();
            }
            tx.success();
            return Response.ok().build();
        }
    }

    @GET
    @Path("{type}")
    public StreamingOutput list(@NotNull @PathParam("type") final Type type) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                try (JsonGenerator generator = jsonFactory.createGenerator(outputStream)) {
                    generator.writeStartArray();
                    try (Transaction tx = graphDb.beginTx();
                         ResourceIterator<Node> nodes = graphDb.findNodes(DynamicLabel.label(type.name()))) {
                        while (nodes.hasNext()) {
                            mapper.writeValue(generator, serializer.serialize(nodes.next()));
                        }
                        tx.success();
                    }
                    generator.writeEndArray();
                }
            }
        };
    }

    @POST
    @Path("{type}")
    public Bundle create(@PathParam("type") Type type, @Valid Bundle bundle) {
        try (Transaction tx = graphDb.beginTx()) {
            Node node = graphDb.createNode(DynamicLabel.label(type.name()));
            node.setProperty(Serializer.ID_KEY, bundle.getId());
            for (Map.Entry<String, Object> entry : bundle.getData().entrySet()) {
                node.setProperty(entry.getKey(), entry.getValue());
            }
            Bundle out = serializer.serialize(node);
            tx.success();
            return out;
        }
    }

    @PUT
    @Path("{type}")
    public Bundle update(@PathParam("type") Type type, @Valid Bundle bundle) {
        try (Transaction tx = graphDb.beginTx()) {
            Node node = graphDb.findNode(
                    DynamicLabel.label(type.name()), Serializer.ID_KEY, bundle.getId());
            for (Map.Entry<String, Object> entry : bundle.getData().entrySet()) {
                node.setProperty(entry.getKey(), entry.getValue());
            }
            for (String key : node.getPropertyKeys()) {
                if (!key.equals(Serializer.ID_KEY) && !bundle.getData().containsKey(key)) {
                    node.removeProperty(key);
                }
            }
            Bundle out = serializer.serialize(node);
            tx.success();
            return out;
        }
    }

    @GET
    @Path("{type}/{id}")
    public Bundle get(
            @NotNull @PathParam("type") Type type,
            @NotNull @PathParam("id") String id) {
        try (Transaction tx = graphDb.beginTx()) {
            Node node = graphDb.findNode(DynamicLabel.label(type.name()), Serializer.ID_KEY, id);
            if (node == null) {
                throw new NotFoundException();
            }
            Bundle bundle = serializer.serialize(node);
            tx.success();
            return bundle;
        }
    }

    @DELETE
    @Path("{type}/{id}")
    public Response delete(
            @NotNull @PathParam("type") Type type,
            @NotNull @PathParam("id") String id) {
        try (Transaction tx = graphDb.beginTx()) {
            Node node = graphDb.findNode(DynamicLabel.label(type.name()), Serializer.ID_KEY, id);
            node.delete();
            tx.success();
            return Response.ok().build();
        }
    }

    @GET
    @Path("{type}/count")
    public int count(
            @NotNull @PathParam("type") Type type) {
        try (Transaction tx = graphDb.beginTx();
             ResourceIterator<Node> nodes = graphDb.findNodes(DynamicLabel.label(type.name()))) {
            int size = Iterators.size(nodes);
            tx.success();
            return size;
        }
    }

    @DELETE
    @Path("{type}/deleteAll")
    public Response deleteAll(
            @NotNull @PathParam("type") Type type) {
        try (Transaction tx = graphDb.beginTx();
             ResourceIterator<Node> nodes = graphDb.findNodes(DynamicLabel.label(type.name()))) {
            while (nodes.hasNext()) {
                nodes.next().delete();
            }
            tx.success();
            return Response.ok().build();
        }
    }
}