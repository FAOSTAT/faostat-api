package org.fao.fenix.faostat.core;

import com.sun.jersey.api.core.InjectParam;
import org.fao.fenix.faostat.beans.DatasourceBean;
import org.fao.fenix.faostat.jdbc.JDBCIterable;

import java.io.IOException;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class FAOSTATAPICore {

    @InjectParam
    JSONSchemaPool jsonSchemaPool;

    @InjectParam
    DatasourcePool datasourcePool;

    @InjectParam
    QueriesPool queriesPool;

    public String getSchema() {
        return jsonSchemaPool.getSchema();
    }

    public JDBCIterable getGroups(String lang) throws IOException, Exception {
        String query = this.getQueriesPool().getQuery("groups", lang);
        DatasourceBean dsb = this.getDatasourcePool().getDatasource("HOME-TEST");
        JDBCIterable i = new JDBCIterable();
        i.query(dsb, query);
        return i;
    }

    public void setJsonSchemaPool(JSONSchemaPool jsonSchemaPool) {
        this.jsonSchemaPool = jsonSchemaPool;
    }

    public JSONSchemaPool getJsonSchemaPool() {
        return jsonSchemaPool;
    }

    public QueriesPool getQueriesPool() {
        return queriesPool;
    }

    public void setQueriesPool(QueriesPool queriesPool) {
        this.queriesPool = queriesPool;
    }

    private String iso2faostat(String lang) {
        switch (lang.toLowerCase()) {
            case "fr": return "F";
            case "es": return "S";
            default: return "E";
        }
    }

    private String faostat2iso(String lang) {
        switch (lang.toUpperCase()) {
            case "F": return "fr";
            case "S": return "es";
            default: return "en";
        }
    }

    public DatasourcePool getDatasourcePool() {
        return datasourcePool;
    }

    public void setDatasourcePool(DatasourcePool datasourcePool) {
        this.datasourcePool = datasourcePool;
    }

}
