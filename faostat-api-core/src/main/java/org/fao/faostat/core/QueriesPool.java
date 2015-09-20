package org.fao.faostat.core;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class QueriesPool {

    private String queriesPath;

    private Map<String, String> queries;

    private String datasource;

    private Handlebars handlebars;

    private Template template;

    public QueriesPool(Resource queriesPath, String datasource) throws Exception {
        this.setDatasource(datasource);
        this.setHandlebars(new Handlebars());
        try {
            this.setQueriesPath(queriesPath.getFile().getPath());
        } catch (IOException e) {                                                               /* pragma: no cover */
            throw new Exception(e.getMessage());                                                /* pragma: no cover */
        }                                                                                       /* pragma: no cover */
    }

    public String getQuery(String id, String lang) throws IOException {
        String query = this.getQueries().get(id);
        this.setTemplate(this.getHandlebars().compileInline(query));
        return this.getTemplate().apply(iso2faostat(lang));
    }

    public String getQuery(String id, Map<String, String> procedureParameters) throws IOException {
        String query = this.getQueries().get(id);
        for (String key : procedureParameters.keySet()) {
            String tmp = "\\{\\{" + key + "\\}\\}";
            query = query.replaceAll(tmp, procedureParameters.get(key));
        }
        System.out.println(query);
        return query;
    }

    public void init() throws IOException {
        Gson g = new Gson();
        String path = this.queriesPath + File.separator + this.getDatasource() + ".json";
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String s = new String(encoded, Charset.defaultCharset());
        Map<String, String> queries = g.fromJson(s, Map.class);
        this.setQueries(queries);
    }

    private String iso2faostat(String lang) {
        switch (lang.toLowerCase()) {
            case "fr": return "F";
            case "es": return "S";
            default: return "E";
        }
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

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
    }

    public Handlebars getHandlebars() {
        return handlebars;
    }

    public void setHandlebars(Handlebars handlebars) {
        this.handlebars = handlebars;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

}