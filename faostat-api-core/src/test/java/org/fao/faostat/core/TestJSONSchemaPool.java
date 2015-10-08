package org.fao.faostat.core;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.fao.faostat.core.JSONSchemaPool;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;

import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class TestJSONSchemaPool extends JerseyTest {

    public TestJSONSchemaPool() {
        super(new WebAppDescriptor.Builder("org.fao.fenix.faostat.core").contextPath("testing")
                .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
                .contextListenerClass(ContextLoaderListener.class).servletClass(SpringServlet.class)
                .requestListenerClass(RequestContextListener.class).build());
    }

    @Test
    public void testGetSchema() {
        JSONSchemaPool j = ContextLoaderListener.getCurrentWebApplicationContext().getBean(JSONSchemaPool.class);
        assertNotNull(j.getSchema());
    }

}