package org.fao.fenix.faostat.core;

import com.google.gson.Gson;
import junit.framework.TestCase;
import org.fao.fenix.faostat.beans.DatasourceBean;

import javax.xml.crypto.Data;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class TestFAOSTATAPICore extends TestCase {

    private FAOSTATAPICore c;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        c = new FAOSTATAPICore();
    }

    public void testSayHallo() {
        assertEquals(c.sayHallo("Guido"), "Hallo, Guido!");
    }

}