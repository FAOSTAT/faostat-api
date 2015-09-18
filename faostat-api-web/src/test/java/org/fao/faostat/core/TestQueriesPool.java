package org.fao.faostat.core;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class TestQueriesPool extends JerseyTest {

    public TestQueriesPool() {
        super(new WebAppDescriptor.Builder("org.fao.fenix.faostat.core").contextPath("testing")
                .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
                .contextListenerClass(ContextLoaderListener.class).servletClass(SpringServlet.class)
                .requestListenerClass(RequestContextListener.class).build());
    }

    @Test
    public void testQueriesPool() {
        QueriesPool p = ContextLoaderListener.getCurrentWebApplicationContext().getBean(QueriesPool.class);
        assertNotNull(p.getDatasource());
        assertNotNull(p.getHandlebars());
        try {
            assertNotNull(p.getQuery("groups", "en"));
            assertEquals("SELECT GroupCode, GroupNameE, Ord FROM Groups ORDER BY Ord", p.getQuery("groups", "en"));
        } catch (IOException e) {
            fail();
        }
    }

}