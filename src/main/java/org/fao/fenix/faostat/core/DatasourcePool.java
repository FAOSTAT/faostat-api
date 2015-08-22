package org.fao.fenix.faostat.core;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.fao.fenix.faostat.beans.DatasourceBean;
import org.fao.fenix.faostat.constants.DRIVER;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class DatasourcePool {

    private String datasourcePath;

    private Map<String, DatasourceBean> datasources;

    public DatasourcePool(Resource datasourcePath) throws Exception {
        datasources = new HashMap<String, DatasourceBean>();
        try {
            this.setDatasourcePath(datasourcePath.getFile().getPath());
        } catch (IOException e) {                                                               /* pragma: no cover */
            throw new Exception(e.getMessage());                                                /* pragma: no cover */
        }                                                                                       /* pragma: no cover */
    }

    public DatasourceBean getDatasource(String id) {
        return this.datasources.get(id.toUpperCase());
    }

    public void init() throws IOException {
        Gson g = new Gson();
        String path = this.datasourcePath + File.separator + "datasources.json";
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String s = new String(encoded, Charset.defaultCharset());
        DatasourceBean[] a = g.fromJson(s, DatasourceBean[].class);
        for (DatasourceBean b : a)
            this.datasources.put(b.getId(), b);
    }

    public void setDatasourcePath(String datasourcePath) {
        this.datasourcePath = datasourcePath;
    }

}