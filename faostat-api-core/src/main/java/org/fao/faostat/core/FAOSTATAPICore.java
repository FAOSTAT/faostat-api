package org.fao.faostat.core;

import com.sun.jersey.api.core.InjectParam;
import org.fao.faostat.beans.DatasourceBean;
import org.fao.faostat.beans.DefaultOptionsBean;
import org.fao.faostat.jdbc.JDBCIterable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                writer.write(createMetadata(o));

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

    public StreamingOutput createDimensionOutputStream(String queryCode, final DefaultOptionsBean o) throws Exception {

        /* Query the DB. */
        JDBCIterable i = getJDBCIterable(queryCode, o);

        /* Store the original result. */
        List<Map<String, String>> l = new ArrayList<Map<String, String>>();
        while (i.hasNext()) {
            l.add(i.nextMap());
        }

        /* Initiate variables. */
        final List<List<Map<String, String>>> dimensions = new ArrayList<List<Map<String, String>>>();
        final List<String> groupJSONs = new ArrayList<String>();

        /* Create groups. */
        List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
        String current = "1";
        for (int idx = 0; idx < l.size(); idx++) {
            if (l.get(idx).get("ListBoxNo").equalsIgnoreCase(current)) {
                groups.add(l.get(idx));
            } else {
                dimensions.add(groups);
                current = l.get(idx).get("ListBoxNo");
                groups = new ArrayList<Map<String, String>>();
                groups.add(l.get(idx));
            }
        }
        dimensions.add(groups);

        /* Iterate over the stored dimensions. */
        for (int z = 0; z < dimensions.size(); z += 1) {

            /* Encode group. */
            String s = "";
            s += "{";
            s += "\"ord\": " + dimensions.get(z).get(0).get("ListBoxNo") + ",";
            s += "\"id\": \"" + dimensions.get(z).get(0).get("VarTypeGroup") + "group\",";
            s += "\"label\": \"TODO\",";
            s += "\"parameter\": \"@List" + dimensions.get(z).get(0).get("ListBoxNo") + "Codes\",";
            s += "\"description\": \"TODO\",";
            s += "\"href\": \"/codes/" + dimensions.get(z).get(0).get("VarTypeGroup") + "group/\",";

            /* Encode subdimensions. */
            s += "\"subdimensions\": [";
            for (int idx = 0; idx < dimensions.get(z).size(); idx += 1) {
                s += "{";
                s += "\"id\": \"" + dimensions.get(z).get(idx).get("TabName").replace(" ", "").toLowerCase() + "\",";
                s += "\"ord\": " + dimensions.get(z).get(idx).get("TabOrder") + ",";
                s += "\"label\": \"" + dimensions.get(z).get(idx).get("TabName") + "\",";
                s += "\"description\": \"" + dimensions.get(z).get(idx).get("TabName") + "\",";
                s += "\"href\": \"/codes/" + dimensions.get(z).get(idx).get("TabName").replace(" ", "").toLowerCase() + "/\"";
                s += "}";
                if (idx < dimensions.get(z).size() - 1)
                    s += ",";
            }
            s += "]";
            s += "}";

            /* Add to the final output. */
            groupJSONs.add(s);

        }

        /* Initiate the output stream. */
        StreamingOutput stream = new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {

                /* Initiate the buffer writer. */
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                /* Initiate the output. */
                writer.write("{");

                /* Add metadata. */
                writer.write(createMetadata(o));

                /* Initiate the array. */
                writer.write("\"data\": [");

                /* Generate an array of objects of arrays. */
                switch (o.getOutputType()) {

                    case "arrays":
                        writer.write("[\"The 'arrays' output type is not yet available for this service. We apologize for any inconvenience.\"]");
                        break;
                    default:
                        for (int i = 0; i < groupJSONs.size(); i++) {
                            writer.write(groupJSONs.get(i).toString());
                            if (i < groupJSONs.size() - 1)
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

    private String createMetadata(DefaultOptionsBean o) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"metadata\": {");
        sb.append("\"datasource\": \"").append(o.getDatasource()).append("\",");
        sb.append("\"output_type\": \"").append(o.getOutputType()).append("\",");
        sb.append("\"api_key\": \"").append(o.getApiKey()).append("\",");
        sb.append("\"client_key\": \"").append(o.getClientKey()).append("\",");
        sb.append("\"parameters\": [");
        int count = 0;
        for (String key : o.getProcedureParameters().keySet()) {
            sb.append("{\"").append(key).append("\": \"").append(o.getProcedureParameters().get(key)).append("\"}");
            if (count < o.getProcedureParameters().keySet().size() - 1) {
                sb.append(",");
                count++;
            }
        }
        sb.append("]");
        sb.append("},");
        return sb.toString();
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
