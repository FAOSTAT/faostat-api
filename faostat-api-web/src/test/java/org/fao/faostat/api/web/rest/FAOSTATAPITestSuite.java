package org.fao.faostat.api.web.rest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    TestGroupsAndDomains.class,
    TestDimensions.class,
    TestCodes.class,
    //TestCodesAllDomains.class,
    TestData.class,
    TestSearch.class,
    TestBulkDownloads.class,
    TestDocuments.class,
    TestDefinitions.class,
    TestSchema.class,

})

public class FAOSTATAPITestSuite {

}