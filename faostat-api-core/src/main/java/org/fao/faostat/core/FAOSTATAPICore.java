package org.fao.faostat.core;

import com.sun.jersey.api.core.InjectParam;
import org.fao.faostat.beans.DatasourceBean;
import org.fao.faostat.beans.DefaultOptionsBean;
import org.fao.faostat.jdbc.JDBCIterable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.Map;

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
        final JDBCIterable i = getJDBCIterable(queryCode, o);

        /* Initiate the output stream. */
        StreamingOutput stream = new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {

                /* Initiate the buffer writer. */
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                /* Initiate the output. */
                writer.write("{");

                /* Add metadata. */
                writer.write("\"metadata\": {},");

                /* Initiate the array. */
                writer.write("\"data\": [");

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

                /* Close the object. */
                writer.write("}");

                /* Flush the writer. */
                writer.flush();

            }

        };

        /* Return stream. */
        return stream;

    }

    private JDBCIterable getJDBCIterable(String queryCode, DefaultOptionsBean o) throws Exception {
        String query = this.getQueriesPool().getQuery(queryCode, o.getProcedureParameters());
        if (query == null)
            throw new Exception("Query \'" + queryCode + "' not found.");
        DatasourceBean dsb = this.getDatasourcePool().getDatasource(o.getDatasource().toUpperCase());
        if (dsb == null)
            throw new Exception("Datasource \'" + o.getDatasource().toUpperCase() + "' not found.");
        JDBCIterable i = new JDBCIterable();
        i.query(dsb, query);
        return i;
    }

    private JDBCIterable getJDBCIterable(String queryCode, String datasource, Map<String, String> procedureParameters) throws Exception {
        String query = this.getQueriesPool().getQuery(queryCode, procedureParameters);
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

    public String iso2faostat(String lang) {
        switch (lang.toLowerCase()) {
            case "fr": return "F";
            case "es": return "S";
            default: return "E";
        }
    }

    public String faostat2iso(String lang) {
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
