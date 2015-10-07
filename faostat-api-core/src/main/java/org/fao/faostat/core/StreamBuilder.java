package org.fao.faostat.core;

import com.google.gson.Gson;
import org.fao.faostat.beans.DatasourceBean;
import org.fao.faostat.beans.DefaultOptionsBean;
import org.fao.faostat.constants.QUERIES;
import org.fao.faostat.jdbc.JDBCIterable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.*;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class StreamBuilder {

    private QUERIES queries;

    private Gson g;

    public StreamBuilder() {
        this.setQueries(new QUERIES());
        this.setG(new Gson());
    }

    public StreamingOutput createOutputStream(String queryCode, DatasourceBean datasourceBean, final DefaultOptionsBean o) throws Exception {

        /* Query the DB. */
        final JDBCIterable i = getJDBCIterable(queryCode, datasourceBean, o);

        /* Initiate the output stream. */
        return new StreamingOutput() {

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

    }

    private List<Map<String, Object>> createDSD(DatasourceBean datasourceBean, DefaultOptionsBean o) {

        /* Initiate DSD. */
        List<Map<String, Object>> dsd = new ArrayList<Map<String, Object>>();
        JDBCIterable i;

        try {

            /* Query DB. */
            i = getJDBCIterable("data_structure", datasourceBean, o);

            /* Iterate over results. */
            while (i.hasNext()) {

                Map<String, String> row = i.nextMap();

                /* Create descriptors for code and label columns. */
                if (!row.get("Col").equalsIgnoreCase("Unit") && !row.get("Col").equalsIgnoreCase("Value")) {
                    Map<String, Object> codeCol = new HashMap<>();
                    codeCol.put("index", Integer.parseInt(row.get("CodeIndex")));
                    codeCol.put("label", row.get("CodeName").toString());
                    codeCol.put("type", "code");
                    codeCol.put("key", row.get("CodeName").toString());
                    dsd.add(codeCol);
                    Map<String, Object> labelCol = new HashMap<>();
                    labelCol.put("index", Integer.parseInt(row.get("NameIndex")));
                    labelCol.put("label", row.get("ColName").toString());
                    labelCol.put("type", "label");
                    labelCol.put("key", row.get("ColName").toString());
                    dsd.add(labelCol);
                }

                /* Create descriptor for the unit. */
                if (row.get("Col").equalsIgnoreCase("Unit")) {
                    Map<String, Object> unitCol = new HashMap<>();
                    unitCol.put("index", Integer.parseInt(row.get("NameIndex")));
                    unitCol.put("label", row.get("ColName").toString());
                    unitCol.put("type", "unit");
                    unitCol.put("key", row.get("ColName").toString());
                    dsd.add(unitCol);
                }

                /* Create descriptor for the value. */
                if (row.get("Col").equalsIgnoreCase("Value")) {
                    Map<String, Object> valueCol = new HashMap<>();
                    valueCol.put("index", Integer.parseInt(row.get("NameIndex")));
                    valueCol.put("label", row.get("ColName").toString());
                    valueCol.put("type", "value");
                    valueCol.put("key", row.get("ColName").toString());
                    dsd.add(valueCol);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Return DSD. */
        return dsd;

    }

    public StreamingOutput createDataOutputStream(final DatasourceBean datasourceBean, final DefaultOptionsBean o) throws Exception {

        /* Query the DB. */
        final JDBCIterable i = getJDBCIterable("data", datasourceBean, o);

        try {

            /* Initiate the output stream. */
            return new StreamingOutput() {

                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {

                    /* Initiate the buffer writer. */
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                    /* Initiate the output. */
                    writer.write("{");

                    /* Add DSD. */
                    o.setDsd(createDSD(datasourceBean, o));

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

        } catch (final Exception e) {
            return new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write("StreamBuilder EXCEPTION\n");
                    if (e.getMessage() != null)
                        writer.write(e.getMessage());
                    writer.flush();
                }
            };
        }

    }

    public StreamingOutput createDomainsOutputStream(String queryCode, DatasourceBean datasourceBean, final DefaultOptionsBean o) throws Exception {

        /* Query the DB. */
        final JDBCIterable i = getJDBCIterable(queryCode, datasourceBean, o);
        final Gson g = new Gson();

        /* Add blacklist and whitelist to the options. */
        List<String> blackList = (List<String>)o.getProcedureParameters().get("blacklist");
        List<String> whiteList = (List<String>)o.getProcedureParameters().get("whitelist");
        o.setBlackList(blackList);
        o.setWhiteList(whiteList);

        /* Initiate the output stream. */
        return new StreamingOutput() {

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

                /* Close the object. */
                writer.write("}");

                /* Flush the writer. */
                writer.flush();

            }

        };

    }

    public StreamingOutput createCodesOutputStream(DatasourceBean datasourceBean, final DefaultOptionsBean o) throws Exception {

        /* Logger. */
        final StringBuilder log = new StringBuilder();
        final Gson g = new Gson();

        /* Add blacklist and whitelist to the options. */
        List<String> blackList = (List<String>)o.getProcedureParameters().get("blacklist");
        List<String> whiteList = (List<String>)o.getProcedureParameters().get("whitelist");
        o.setBlackList(blackList);
        o.setWhiteList(whiteList);

        /* Boolean parameters. */
        boolean show_lists = Boolean.parseBoolean(o.getProcedureParameters().get("show_lists").toString());
        boolean show_full_metadata = Boolean.parseBoolean(o.getProcedureParameters().get("show_full_metadata").toString());
        boolean group_subdimensions = Boolean.parseBoolean(o.getProcedureParameters().get("group_subdimensions").toString());

        try {

            /* Get domain's dimensions. */
            List<List<Map<String, String>>> dimensions = getDomainDimensions("dimensions", datasourceBean, o);

            /* Organize data in an object. */
            final List<Map<String, Object>> structuredDimensions = organizeDomainDimensions(dimensions);

            /* Get the dimension of interest. */
            Map<String, Object> dimension = null;
            String dimensionCode = o.getProcedureParameters().get("id").toString();
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
            List<Map<String, Object>> codes = new ArrayList<>();
            Map<String, Object> row;

            /* Get subdimensions. */
            ArrayList<Map<String, Object>> subDimensions = (ArrayList<Map<String, Object>>) dimension.get("subdimensions");

            if (subDimensions != null) {

                /* Fetch codes for each sub-dimension. */
                for (Map<String, Object> m : subDimensions) {

                    /* Define options to fetch codes. */
                    DefaultOptionsBean subDimensionOptions = new DefaultOptionsBean();
                    subDimensionOptions.addParameter("domain_code", o.getProcedureParameters().get("domain_code"));
                    subDimensionOptions.addParameter("lang", o.getProcedureParameters().get("lang"));
                    subDimensionOptions.addParameter("dimension", m.get("ListBoxNo").toString());
                    subDimensionOptions.addParameter("subdimension", m.get("TabOrder").toString());
                    JDBCIterable subDimensionIterable = getJDBCIterable("codes", datasourceBean, subDimensionOptions);

                    /* Iterate over codes. */
                    while (subDimensionIterable.hasNext()) {
                        row = createCode(subDimensionIterable.nextMap());
                        row.put("subdimension_id", m.get("TabName").toString().replace(" ", "").toLowerCase());
                        row.put("subdimension_ord", m.get("TabOrder").toString());
                        row.put("subdimension_label", m.get("TabName").toString());
                        log.append("\t\tcontains ").append(row.get("code").toString().toUpperCase()).append("? ").append(Arrays.asList(o.getBlackList()).contains(row.get("code").toString().toUpperCase())).append("\n");
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
                JDBCIterable subDimensionIterable = getJDBCIterable("codes", datasourceBean, subDimensionOptions);
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

            /* Group subdimensions, if required. */
            final List<Map<String, Object>> output = new ArrayList<>();
            if (group_subdimensions) {

                /* Prepare output. */
                for (Map<String, Object> m : subDimensions) {
                    Map<String, Object> subdimension = new HashMap<>();
                    subdimension.put("metadata", new HashMap<String, Object>());
                    subdimension.put("data", new ArrayList<HashMap<String, Object>>());
                    output.add(subdimension);
                }

                /* Group codes. */
                String current_id = codes.get(0).get("subdimension_id").toString();
                String current_ord = codes.get(0).get("subdimension_ord").toString();
                String current_label = codes.get(0).get("subdimension_label").toString();
                int current_idx = 0;
                for (int i = 0; i < codes.size(); i += 1) {

                    /* Current code. */
                    Map<String, Object> c = codes.get(i);

                    if (c.get("subdimension_id").toString().equalsIgnoreCase(current_id)) {
                        ((ArrayList<Map<String, Object>>)output.get(current_idx).get("data")).add(c);
                    } else {

                        /* Write metadata. */
                        ((HashMap<String, Object>)output.get(current_idx).get("metadata")).put("id", current_id);
                        ((HashMap<String, Object>)output.get(current_idx).get("metadata")).put("ord", current_ord);
                        ((HashMap<String, Object>)output.get(current_idx).get("metadata")).put("label", current_label);

                        /* Reset parameters. */
                        current_id = codes.get(i).get("subdimension_id").toString();
                        current_ord = codes.get(i).get("subdimension_ord").toString();
                        current_label = codes.get(i).get("subdimension_label").toString();
                        current_idx += 1;
                        ((ArrayList<Map<String, Object>>)output.get(current_idx).get("data")).add(c);

                    }
                }

                /* Write metadata for the last iteration. */
                ((HashMap<String, Object>)output.get(current_idx).get("metadata")).put("id", current_id);
                ((HashMap<String, Object>)output.get(current_idx).get("metadata")).put("ord", current_ord);
                ((HashMap<String, Object>)output.get(current_idx).get("metadata")).put("label", current_label);

            } else {
                output.addAll(codes);
            }

            /* Initiate the output stream. */
            return new StreamingOutput() {

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
                    writer.write("\"data\": ");

                    /* Generate an array of objects of arrays. */
                    switch (o.getOutputType()) {

                        case "arrays":
                            writer.write("[\"The 'arrays' output type is not yet available for this service. We apologize for any inconvenience.\"]");
                            break;
                        default:
                            Gson g = new Gson();
                            writer.write(g.toJson(output));
                            break;

                    }

                    /* Close the object. */
                    writer.write("}");

                    /* Flush the writer. */
                    writer.flush();

                }

            };

        } catch (Exception e) {
            return new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    log.append("\n\n\nEXCEPTION!");
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write(log.toString());
                    writer.flush();
                }
            };
        }

    }

    public StreamingOutput createDimensionOutputStream(String queryCode, DatasourceBean datasourceBean, final DefaultOptionsBean o) throws Exception {

        /* Initiate variables. */
        final List<List<Map<String, String>>> dimensions = getDomainDimensions("dimensions", datasourceBean, o);
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
        return new StreamingOutput() {

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

    }

    private List<List<Map<String, String>>> getDomainDimensions(String queryCode, DatasourceBean datasourceBean, DefaultOptionsBean o) throws Exception {

        /* Query the DB. */
        JDBCIterable i = getJDBCIterable(queryCode, datasourceBean, o);

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

    private JDBCIterable getJDBCIterable(String queryCode, DatasourceBean datasourceBean, DefaultOptionsBean o) throws Exception {
        String query = this.getQueries().getQuery(queryCode, o.getProcedureParameters());
        if (query == null)
            throw new Exception("Query \'" + queryCode + "' not found.");
        if (datasourceBean == null)
            throw new Exception("Datasource \'" + o.getDatasource().toUpperCase() + "' not found.");
        JDBCIterable i = new JDBCIterable();
        i.query(datasourceBean, query);
        return i;
    }

    private String createMetadata(DefaultOptionsBean o) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"metadata\": {");
        if (o.getDsd() != null && o.getDsd().size() > 0) {
            sb.append("\"dsd\": [");
            for (int a = 0; a < o.getDsd().size(); a += 1) {
                sb.append("{");
                HashMap<String, Object> col = (HashMap<String, Object>)o.getDsd().get(a);
                int counter = 0;
                for (String key : col.keySet()) {
                    sb.append("\"").append(key).append("\": \"").append(col.get(key)).append("\"");
                    if (counter < col.keySet().size() - 1)
                        sb.append(",");
                    counter += 1;
                }
                sb.append("}");
                if (a < o.getDsd().size() - 1)
                    sb.append(",");
            }
            sb.append("],");
        }
        sb.append("\"datasource\": \"").append(o.getDatasource()).append("\",");
        sb.append("\"output_type\": \"").append(o.getOutputType()).append("\",");
        sb.append("\"api_key\": \"").append(o.getApiKey()).append("\",");
        sb.append("\"client_key\": \"").append(o.getClientKey()).append("\",");
        sb.append("\"parameters\": {");
        int count = 0;
        for (String key : o.getProcedureParameters().keySet()) {
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
            return Boolean.parseBoolean(o.getProcedureParameters().get("show_lists").toString());
        } else {

            /* Check the blacklist. */
            if (o.getBlackList() != null && o.getBlackList().size() > 0) {
                if (o.getBlackList().contains(row.get("code").toString())) {
                    return false;
                }
            }

            /* Check the whitelist. */
            if (o.getWhiteList() != null && o.getWhiteList().size() > 0) {
                if (!o.getWhiteList().contains(row.get("code").toString())) {
                    return false;
                }
            }

        }

        /* Return. */
        return true;

    }

    private boolean isAdmissibleDBRow(Map<String, String> row, DefaultOptionsBean o) {

        /* Check the blacklist. */
        if (o.getBlackList() != null && o.getBlackList().size() > 0) {
            if (o.getBlackList().contains(row.get("code").toUpperCase())) {
                return false;
            }
        }

        /* Check the whitelist. */
        if (o.getWhiteList() != null && o.getWhiteList().size() > 0) {
            if (!o.getWhiteList().contains(row.get("code").toUpperCase())) {
                return false;
            }
        }

        /* Return. */
        return true;

    }

    public QUERIES getQueries() {
        return queries;
    }

    public void setQueries(QUERIES queries) {
        this.queries = queries;
    }

    public Gson getG() {
        return g;
    }

    public void setG(Gson g) {
        this.g = g;
    }

}