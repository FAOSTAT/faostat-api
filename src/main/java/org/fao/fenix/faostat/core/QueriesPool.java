package org.fao.fenix.faostat.core;

import com.google.gson.Gson;
import org.fao.fenix.faostat.beans.DatasourceBean;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class QueriesPool {

    private String queriesPath;

    private Map<String, Map<String, String>> queries;

    private String datasource;

    public QueriesPool(Resource queriesPath, String datasource) throws Exception {
        queries = new HashMap<String, Map<String, String>>();
        this.setDatasource(datasource);
        try {
            this.setQueriesPath(queriesPath.getFile().getPath());
        } catch (IOException e) {                                                               /* pragma: no cover */
            throw new Exception(e.getMessage());                                                /* pragma: no cover */
        }                                                                                       /* pragma: no cover */
    }

    public String getQuery(String id, String lang) {
        return this.getQueries().get(id.toLowerCase()).get(lang.toLowerCase());
    }

    public void init() throws IOException {
        Gson g = new Gson();
        String path = this.queriesPath + File.separator + this.getDatasource() + ".json";
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String s = new String(encoded, Charset.defaultCharset());
        Map<String, Map<String, String>> queries = g.fromJson(s, Map.class);
        this.setQueries(queries);
    }

    public void setQueriesPath(String queriesPath) {
        this.queriesPath = queriesPath;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Map<String, Map<String, String>> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, Map<String, String>> queries) {
        this.queries = queries;
    }

}