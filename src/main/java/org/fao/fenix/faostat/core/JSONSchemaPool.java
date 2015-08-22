package org.fao.fenix.faostat.core;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class JSONSchemaPool {

    private String schemaPath;

    private String schema;

    public JSONSchemaPool(Resource datasourcePath) throws Exception {
        try {
            this.setDatasourcePath(datasourcePath.getFile().getPath());
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void init() throws IOException {
        String path = this.schemaPath + File.separator + "schema.json";
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        this.setSchema(new String(encoded, Charset.defaultCharset()));
    }

    public void setDatasourcePath(String datasourcePath) {
        this.schemaPath = datasourcePath;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}