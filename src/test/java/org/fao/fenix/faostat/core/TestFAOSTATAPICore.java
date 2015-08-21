package org.fao.fenix.faostat.core;

import junit.framework.TestCase;

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