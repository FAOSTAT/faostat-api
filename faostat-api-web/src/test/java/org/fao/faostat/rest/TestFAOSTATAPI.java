package org.fao.faostat.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class TestFAOSTATAPI extends JerseyTest {

    public TestFAOSTATAPI() {
        super(new WebAppDescriptor.Builder("org.fao.faostat.rest").contextPath("testing")
                .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
                .contextListenerClass(ContextLoaderListener.class).servletClass(SpringServlet.class)
                .requestListenerClass(RequestContextListener.class).build());
    }

    @Test
    public void testGetSchema(){
        WebResource ws = resource().path("v1.0/");
        ClientResponse response = ws.get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String out = response.getEntity(String.class);
        assertNotNull(out);
    }

}