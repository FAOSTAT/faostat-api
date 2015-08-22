package org.fao.fenix.faostat.core;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.fao.fenix.faostat.beans.DatasourceBean;
import org.fao.fenix.faostat.constants.DRIVER;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class TestDatasourcePool extends JerseyTest {

    public TestDatasourcePool() {
        super(new WebAppDescriptor.Builder("org.fao.fenix.faostat.core").contextPath("testing")
                .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
                .contextListenerClass(ContextLoaderListener.class).servletClass(SpringServlet.class)
                .requestListenerClass(RequestContextListener.class).build());
    }

    @Test
    public void testGetDatasource() {
        DatasourcePool p = ContextLoaderListener.getCurrentWebApplicationContext().getBean(DatasourcePool.class);
        DatasourceBean b = p.getDatasource("FAOSTAT");
        assertNotNull(b);
        assertEquals(b.getDbName(), "Warehouse");
        assertEquals(b.getId(), "FAOSTAT");
        assertEquals(b.getPassword(), "w@reh0use");
        assertEquals(b.getUrl(), "jdbc:sqlserver://HQWPRFAOSTATDB1\\Dissemination;databaseName=Warehouse;");
        assertEquals(b.getUsername(), "Warehouse");
        assertEquals(b.getDriver(), "SQLServer2000");
        assertEquals(b.isCreate(), false);
        assertEquals(b.isDelete(), false);
        assertEquals(b.isRetrieve(), true);
        assertEquals(b.isUpdate(), false);
    }

}