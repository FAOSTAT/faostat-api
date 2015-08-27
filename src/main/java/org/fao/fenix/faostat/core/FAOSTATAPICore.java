package org.fao.fenix.faostat.core;

import com.sun.jersey.api.core.InjectParam;
import org.fao.fenix.faostat.beans.DatasourceBean;
import org.fao.fenix.faostat.beans.DefaultOptionsBean;
import org.fao.fenix.faostat.jdbc.JDBCIterable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

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

    public StreamingOutput createOutputStream(String queryCode, final DefaultOptionsBean o) throws Exception {

        /* Query the DB. */
        final JDBCIterable i = getJDBCIterable(queryCode, o.getDatasource(), o.getLang());

        /* Initiate the output stream. */
        StreamingOutput stream = new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {

                /* Initiate the buffer writer. */
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                /* Initiate the array. */
                writer.write("[");

                /* Generate an array of objects of arrays. */
                switch (o.getOutputType()) {

                    case "arrays":
                        while (i.hasNext()) {
                            writer.write(i.nextArray());
                            if (i.hasNext())
                                writer.write(",");
                        }
                        break;
                    default:
                        while (i.hasNext()) {
                            writer.write(i.nextJSON());
                            if (i.hasNext())
                                writer.write(",");
                        }
                        break;

                }

                /* Close the array. */
                writer.write("]");

                /* Flush the writer. */
                writer.flush();

            }

        };

        /* Return stream. */
        return stream;

    }

    private JDBCIterable getJDBCIterable(String queryCode, String datasource, String lang) throws Exception {
        String query = this.getQueriesPool().getQuery(queryCode, lang);
        if (query == null)
            throw new Exception("Query \'" + queryCode + "' not found.");
        DatasourceBean dsb = this.getDatasourcePool().getDatasource(datasource.toUpperCase());
        if (dsb == null)
            throw new Exception("Datasource \'" + datasource.toUpperCase() + "' not found.");
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
