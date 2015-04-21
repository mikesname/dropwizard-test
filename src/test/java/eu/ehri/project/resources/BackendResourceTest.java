package eu.ehri.project.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import eu.ehri.project.core.Bundle;
import eu.ehri.project.core.Type;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.ConstraintViolationException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.StreamingOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BackendResourceTest {

    private GraphDatabaseService graphDb;
    private BackendResource resource;

    private final Bundle testBundle = Bundle.create("id1", Type.doc,
            ImmutableMap.<String, Object>of("test", "data"));

    @Before
    public void setUp() throws Exception {
        graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
        resource = new BackendResource(graphDb);
    }

    @After
    public void tearDown() throws Exception {
        graphDb.shutdown();
    }

    @Test
    public void testCreateSchema() throws Exception {
        try (Transaction tx = graphDb.beginTx()) {
            assertTrue(Iterables.isEmpty(graphDb.schema().getConstraints()));
        }
        resource.createSchema();
        try (Transaction tx = graphDb.beginTx()) {
            assertFalse(Iterables.isEmpty(graphDb.schema().getConstraints()));
        }
    }

    @Test
    public void testList() throws Exception {
        assertTrue(readStream(resource.list(Type.doc)).isEmpty());
        resource.create(Type.doc, testBundle);
        List<Bundle> list = readStream(resource.list(Type.doc));
        assertFalse(list.isEmpty());
        assertEquals(testBundle, list.get(0));
    }

    @Test
    public void testCreate() throws Exception {
        Bundle out = resource.create(Type.doc, testBundle);
        assertEquals(testBundle, out);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testCreateWithIntegrityError() throws Exception {
        resource.createSchema();
        resource.create(Type.doc, testBundle);
        resource.create(Type.doc, testBundle);
    }

    @Test
    public void testUpdate() throws Exception {
        resource.create(Type.doc, testBundle);

        Bundle updated = Bundle.create("id1", Type.doc,
                ImmutableMap.<String, Object>of("test", "data", "more", "data"));
        resource.update(Type.doc, updated);
        Bundle out = resource.get(Type.doc, "id1");
        assertEquals(out, updated);
    }

    @Test
    public void testGet() throws Exception {
        Bundle out = resource.create(Type.doc, testBundle);
        assertEquals(testBundle, out);
        assertEquals(testBundle, resource.get(Type.doc, "id1"));

    }

    @Test(expected = NotFoundException.class)
    public void testGet404() throws Exception {
        resource.get(Type.doc, "idontexist");
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(0, resource.count(Type.doc));
        resource.create(Type.doc, testBundle);
        assertEquals(1, resource.count(Type.doc));
    }

    @Test
    public void testDelete() throws Exception {
        resource.create(Type.doc, testBundle);
        assertEquals(1, resource.count(Type.doc));
        resource.delete(Type.doc, testBundle.getId());
        assertEquals(0, resource.count(Type.doc));
    }

    @Test
    public void testDeleteAll() throws Exception {
        resource.create(Type.doc, testBundle);
        assertEquals(1, resource.count(Type.doc));
        resource.create(Type.doc, testBundle.withId("id2"));
        assertEquals(2, resource.count(Type.doc));
        resource.deleteAll(Type.doc);
        assertEquals(0, resource.count(Type.doc));
    }

    private List<Bundle> readStream(StreamingOutput streamingOutput) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        streamingOutput.write(outputStream);
        outputStream.close();
        TypeReference<ArrayList<Bundle>> typeRef = new TypeReference<ArrayList<Bundle>>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(outputStream.toByteArray(), typeRef);
    }
}