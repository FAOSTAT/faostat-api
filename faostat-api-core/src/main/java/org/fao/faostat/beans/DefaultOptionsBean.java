package org.fao.faostat.beans;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class DefaultOptionsBean {

    private String datasource;

    private String apiKey;

    private String clientKey;

    private String outputType;

    private String lang;

    public DefaultOptionsBean(String datasource, String lang, String apiKey, String clientKey, String outputType) {
        this.setLang(lang);
        this.setApiKey(apiKey);
        this.setClientKey(clientKey);
        this.setOutputType(outputType);
        this.setDatasource(datasource);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    @Override
    public String toString() {
        return "ConfigBean{" +
                "datasource='" + datasource + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", clientKey='" + clientKey + '\'' +
                ", outputType='" + outputType + '\'' +
                '}';
    }

}
