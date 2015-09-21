package org.fao.faostat.beans;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class TestDefaultOptionsBean extends TestCase {

    private DefaultOptionsBean b;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        b = new DefaultOptionsBean("datasource", "apiKey", "clientKey", "outputType");
    }

    public void testSetters() {
        assertEquals("datasource", b.getDatasource());
        assertEquals("apiKey", b.getApiKey());
        assertEquals("clientKey", b.getClientKey());
        assertEquals("outputType", b.getOutputType());
    }

}