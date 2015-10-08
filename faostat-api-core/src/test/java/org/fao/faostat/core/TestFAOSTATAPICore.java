package org.fao.faostat.core;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.fao.faostat.core.FAOSTATAPICore;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;

import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class TestFAOSTATAPICore extends JerseyTest {

    public TestFAOSTATAPICore() {
        super(new WebAppDescriptor.Builder("org.fao.fenix.faostat.core").contextPath("testing")
                .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
                .contextListenerClass(ContextLoaderListener.class).servletClass(SpringServlet.class)
                .requestListenerClass(RequestContextListener.class).build());
    }

    FAOSTATAPICore c;

    @Test
    public void testGetSchema() {
        c = ContextLoaderListener.getCurrentWebApplicationContext().getBean(FAOSTATAPICore.class);
        assertNotNull(c.getSchema());
    }

    @Test
    public void testGetJSONSchemaPool() {
        c = ContextLoaderListener.getCurrentWebApplicationContext().getBean(FAOSTATAPICore.class);
        assertNotNull(c.getJsonSchemaPool());
    }

}