package org.fao.fenix.faostat.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class TestFAOSTATAPI extends JerseyTest {

    private WebResource ws;

    public TestFAOSTATAPI() {
        super(new WebAppDescriptor.Builder("org.fao.fenix.faostat.rest").build());
    }

    @Test
    public void testHello(){
        ws = resource().path("v1.0/Guido");
        ClientResponse response = ws.get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String out = response.getEntity(String.class);
        assertEquals("Hallo, Guido!", out);
    }

}