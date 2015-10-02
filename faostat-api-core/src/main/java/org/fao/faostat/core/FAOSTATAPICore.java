package org.fao.faostat.core;

import com.google.gson.Gson;
import com.sun.jersey.api.core.InjectParam;
import org.fao.faostat.beans.DatasourceBean;
import org.fao.faostat.beans.DefaultOptionsBean;
import org.fao.faostat.jdbc.JDBCIterable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.*;

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

    public StreamingOutput createDomainsOutputStream(String queryCode, final DefaultOptionsBean o) throws Exception {

        /* Query the DB. */
        final JDBCIterable i = getJDBCIterable(queryCode, o);
        final Gson g = new Gson();

        /* Add blacklist and whitelist to the options. */
        final String[] blackList = g.fromJson(o.getProcedureParameters().get("blacklist"), String[].class);
        final String[] whiteList = g.fromJson(o.getProcedureParameters().get("whitelist"), String[].class);
        o.setBlackList(blackList);
        o.setWhiteList(whiteList);

        /* Initiate the output stream. */
        StreamingOutput stream = new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {

                final List<Map<String, String>> codes = new ArrayList<>();
                while (i.hasNext()) {
                    Map<String, String> dbRow = i.nextMap();
                    if (isAdmissibleDBRow(dbRow, o))
                        codes.add(dbRow);
                }

                /* Initiate the buffer writer. */
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                /* Initiate the output. */
                writer.write("{");

                /* Add metadata. */
                writer.write(createMetadata(o));

                /* Initiate the array. */
//                writer.write("\"data\": [");
                writer.write("\"data\": ");

                /* Generate an array of objects of arrays. */
                switch (o.getOutputType()) {

                    case "arrays":
                        writer.write("[\"The 'arrays' output type is not yet available for this service. We apologize for any inconvenience.\"]");
                        break;
                    default:
                        writer.write(g.toJson(codes));
                        break;

                }

                /* Close the array. */
//                writer.write("]");

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
        final Gson g = new Gson();

        /* Add blacklist and whitelist to the options. */
        final String[] blackList = g.fromJson(o.getProcedureParameters().get("blacklist"), String[].class);
        final String[] whiteList = g.fromJson(o.getProcedureParameters().get("whitelist"), String[].class);
        o.setBlackList(blackList);
        o.setWhiteList(whiteList);

        try {

            /* Get domain's dimensions. */
            List<List<Map<String, String>>> dimensions = getDomainDimensions("dimensions", o);

            /* Organize data in an object. */
            final List<Map<String, Object>> structuredDimensions = organizeDomainDimensions(dimensions);

            /* Get the dimension of interest. */
            Map<String, Object> dimension = null;
            String dimensionCode = o.getProcedureParameters().get("id");
            for (Map<String, Object> m : structuredDimensions) {
                if (m.get("id").toString().equalsIgnoreCase(dimensionCode)) {
                    dimension = m;
                    break;
                }
                ArrayList<Map<String, Object>> subdimensions = (ArrayList<Map<String, Object>>) m.get("subdimensions");
                for (Map<String, Object> subm : subdimensions) {
                    if (subm.get("id").toString().equalsIgnoreCase(dimensionCode)) {
                        dimension = subm;
                        break;
                    }
                }
            }

            /* Prepare output. */
            final List<Map<String, Object>> codes = new ArrayList<>();
            Map<String, Object> row;

            /* Get subdimensions. */
            ArrayList<Map<String, Object>> subDimensions = (ArrayList<Map<String, Object>>) dimension.get("subdimensions");

            if (subDimensions != null) {

                /* Fetch codes for each sub-dimension. */
                for (Map<String, Object> m : subDimensions) {
                    DefaultOptionsBean subDimensionOptions = new DefaultOptionsBean();
                    subDimensionOptions.addParameter("domain_code", o.getProcedureParameters().get("domain_code"));
                    subDimensionOptions.addParameter("lang", o.getProcedureParameters().get("lang"));
                    subDimensionOptions.addParameter("dimension", m.get("ListBoxNo").toString());
                    subDimensionOptions.addParameter("subdimension", m.get("TabOrder").toString());
                    JDBCIterable subDimensionIterable = getJDBCIterable("codes", subDimensionOptions);
                    while (subDimensionIterable.hasNext()) {
                        row = createCode(subDimensionIterable.nextMap());
                        if (isAdmissibleCode(row, o))
                            codes.add(row);
                    }
                }

            } else {

                /* Fetch codes. */
                DefaultOptionsBean subDimensionOptions = new DefaultOptionsBean();
                subDimensionOptions.addParameter("domain_code", o.getProcedureParameters().get("domain_code"));
                subDimensionOptions.addParameter("lang", o.getProcedureParameters().get("lang"));
                subDimensionOptions.addParameter("dimension", dimension.get("ListBoxNo").toString());
                subDimensionOptions.addParameter("subdimension", dimension.get("TabOrder").toString());
                JDBCIterable subDimensionIterable = getJDBCIterable("codes", subDimensionOptions);
                while (subDimensionIterable.hasNext()) {
                    row = createCode(subDimensionIterable.nextMap());
                    if (isAdmissibleCode(row, o))
                        codes.add(row);
                }

            }

            /* Add children. */
            for (Map<String, Object> c1 : codes) {
                if (c1.get("parent") != null) {
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

//                    writer.write("\"log\": " + g.toJson(blacklist) + ",");
//                    writer.write("\"log2\": " + log + ",");

                    /* Initiate the array. */
//                    writer.write("\"data\": [");
                    writer.write("\"data\": ");

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
//                    writer.write("]");

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

    private Map<String, Object> createCode(Map<String, String> dbRow) {
        Map<String, Object> code = new HashMap<>();
        code.put("code", dbRow.get("Code"));
        code.put("label", dbRow.get("Label"));
        code.put("ord", dbRow.get("Order"));
        code.put("parent", dbRow.get("Parent"));
        code.put("description", "TODO");
        code.put("aggregate_type", dbRow.get("AggregateType"));
        code.put("children", new ArrayList<Map<String, Object>>());
        return code;
    }

    private boolean isAdmissibleCode(Map<String, Object> row, DefaultOptionsBean o) {

        /* Check wheter the code is a list. */
        if (row.get("aggregate_type").equals(">")) {
            return Boolean.parseBoolean(o.getProcedureParameters().get("show_lists"));
        } else {

            /* Check the blacklist. */
            if (o.getBlackList() != null && o.getBlackList().length > 0) {
                if (Arrays.asList(o.getBlackList()).contains(row.get("code").toString())) {
                    return false;
                }
            }

            /* Check the whitelist. */
            if (o.getWhiteList() != null && o.getWhiteList().length > 0) {
                if (!Arrays.asList(o.getWhiteList()).contains(row.get("code").toString())) {
                    return false;
                }
            }

        }

        /* Return. */
        return true;

    }

    private boolean isAdmissibleDBRow(Map<String, String> row, DefaultOptionsBean o) {

        /* Check the blacklist. */
        if (o.getBlackList() != null && o.getBlackList().length > 0) {
            if (Arrays.asList(o.getBlackList()).contains(row.get("code").toUpperCase())) {
                return false;
            }
        }

        /* Check the whitelist. */
        if (o.getWhiteList() != null && o.getWhiteList().length > 0) {
            if (!Arrays.asList(o.getWhiteList()).contains(row.get("code").toUpperCase())) {
                return false;
            }
        }

        /* Return. */
        return true;

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
            m.put("id", m.get("TabName").replaceAll(" ", "").toLowerCase());
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
        sb.append("\"parameters\": {");
        int count = 0;
        for (String key : o.getProcedureParameters().keySet()) {
//            sb.append("{\"").append(key).append("\": \"").append(o.getProcedureParameters().get(key)).append("\"}");
            sb.append("\"").append(key).append("\": \"").append(o.getProcedureParameters().get(key)).append("\"");
            if (count < o.getProcedureParameters().keySet().size() - 1) {
                sb.append(",");
                count++;
            }
        }
        sb.append("}");
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
