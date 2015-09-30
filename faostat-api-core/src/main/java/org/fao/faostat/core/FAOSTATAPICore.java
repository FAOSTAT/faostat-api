package org.fao.faostat.core;

import com.google.gson.Gson;
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

        /* Initiate variables. */
        final List<List<Map<String, String>>> dimensions = getDomainDimensions("dimensions", o);
        final List<String> groupJSONs = new ArrayList<String>();

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

    public StreamingOutput createCodesOutputStream(String queryCode, final DefaultOptionsBean o) throws Exception {

        /* Logger. */
        final StringBuilder log = new StringBuilder();

        try {

            /* Get domain's dimensions. */
            List<List<Map<String, String>>> dimensions = getDomainDimensions("dimensions", o);

            /* Organize data in an object. */
            final List<Map<String, Object>> structuredDimensions = organizeDomainDimensions(dimensions);

            /* Get the dimension of interest. */
            Map<String, Object> dimension = null;
            String dimensionCode = o.getProcedureParameters().get("id");
            for (Map<String, Object> m : structuredDimensions) {
                log.append(m).append("\n");
                if (m.get("id").toString().equalsIgnoreCase(dimensionCode)) {
                    dimension = m;
                    break;
                }
            }

            /* Get subdimensions. */
            ArrayList<Map<String, Object>> subdimensions = (ArrayList<Map<String, Object>>) dimension.get("subdimensions");

            /* Prepare output. */
            final List<Map<String, Object>> codes = new ArrayList<>();
            Map<String, Object> row;
            Map<String, String> tmp;

            /* Fetch codes for each sub-dimension. */
            for (Map<String, Object> m : subdimensions) {
                DefaultOptionsBean subDimensionOptions = new DefaultOptionsBean();
                subDimensionOptions.addParameter("domain_code", o.getProcedureParameters().get("domain_code"));
                subDimensionOptions.addParameter("lang", o.getProcedureParameters().get("lang"));
                subDimensionOptions.addParameter("dimension", m.get("ListBoxNo").toString());
                subDimensionOptions.addParameter("subdimension", m.get("TabOrder").toString());
                JDBCIterable subDimensionIterable = getJDBCIterable("codes", subDimensionOptions);
                while (subDimensionIterable.hasNext()) {
                    tmp = subDimensionIterable.nextMap();
                    row = new HashMap<>();
                    row.put("code", tmp.get("Code"));
                    row.put("label", tmp.get("Label"));
                    row.put("ord", tmp.get("Order"));
                    row.put("parent", tmp.get("Parent"));
                    row.put("description", "TODO");
                    row.put("aggregate_type", tmp.get("AggregateType"));
                    row.put("children", new ArrayList<Map<String, Object>>());
                    codes.add(row);
                }
            }

            /* Add children. */
            for (Map<String, Object> c1 : codes) {
                if (c1.get("parent") != null) {
                    log.append("code ").append(c1.get("code")).append(" has parent ").append(c1.get("parent")).append("\n");
                    for (Map<String, Object> c2 : codes) {
                        if (c2.get("code").toString().equalsIgnoreCase(c1.get("parent").toString())) {
                            ((ArrayList<Map<String, Object>>)c2.get("children")).add(c1);
                        }
                    }
                }
            }

            /* Initiate the output stream. */
            StreamingOutput stream = new StreamingOutput() {

                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {

                    /* Initiate the buffer writer. */
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                    /* Initiate the output. */
                    writer.write("{");

                    /* Add procedure parameter to the metadata. */
                    o.addParameter("parameter", structuredDimensions.get(0).get("parameter").toString());

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
                            Gson g = new Gson();
                            writer.write(g.toJson(codes));
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

        } catch (Exception e) {
            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    log.append("\n\n\nEXCEPTION!");
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write(log.toString());
                    writer.flush();
                }
            };
            return stream;
        }

    }

    private List<List<Map<String, String>>> getDomainDimensions(String queryCode, DefaultOptionsBean o) throws Exception {

        /* Query the DB. */
        JDBCIterable i = getJDBCIterable(queryCode, o);

        /* Store the original result. */
        List<Map<String, String>> l = new ArrayList<>();
        while (i.hasNext()) {
            l.add(i.nextMap());
        }

        /* Initiate variables. */
        List<List<Map<String, String>>> dimensions = new ArrayList<>();

        /* Create groups. */
        List<Map<String, String>> groups = new ArrayList<>();
        String current = "1";
        for (Map<String, String> m : l) {
            if (m.get("ListBoxNo").equalsIgnoreCase(current)) {
                groups.add(m);
            } else {
                dimensions.add(groups);
                current = m.get("ListBoxNo");
                groups = new ArrayList<>();
                groups.add(m);
            }
        }
        dimensions.add(groups);

        /* Return output. */
        return dimensions;

    }

    private List<Map<String, Object>> organizeDomainDimensions(List<List<Map<String, String>>> dimensions) {
        List<Map<String, Object>> out = new ArrayList<>();
        List<String> idsBuffer = new ArrayList<>();
        for (List<Map<String, String>> dimension : dimensions) {
            Map<String, Object> tmp = new HashMap<>();
            if (!idsBuffer.contains(dimension.get(0).get("VarTypeGroup") + "group")) {
                idsBuffer.add(dimension.get(0).get("VarTypeGroup") + "group");
                tmp.put("id", dimension.get(0).get("VarTypeGroup") + "group");
            } else {
                tmp.put("id", dimension.get(0).get("VarTypeGroup") + "group2");
            }
            tmp.put("ord", dimension.get(0).get("ListBoxNo"));
            tmp.put("label", "TODO");
            tmp.put("description", "TODO");
            tmp.put("parameter", "@List" + dimension.get(0).get("ListBoxNo") + "Codes");
            tmp.put("href", "/codes/" + dimension.get(0).get("VarTypeGroup") + "group/");
            tmp.put("subdimensions", new ArrayList<Map<String, Object>>());
            for (Map<String, String> subdimension : dimension) {
                ((ArrayList)tmp.get("subdimensions")).add(subdimension);
            }
            out.add(tmp);
        }
        return out;
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
        String query = getQuery(queryCode, o);
        if (query == null)
            throw new Exception("Query \'" + queryCode + "' not found.");
        DatasourceBean dsb = this.getDatasourcePool().getDatasource(o.getDatasource().toUpperCase());
        if (dsb == null)
            throw new Exception("Datasource \'" + o.getDatasource().toUpperCase() + "' not found.");
        JDBCIterable i = new JDBCIterable();
        i.query(dsb, query);
        return i;
    }

    private String getQuery(String queryCode, DefaultOptionsBean o) throws Exception {
        return this.getQueriesPool().getQuery(queryCode, o.getProcedureParameters());
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
