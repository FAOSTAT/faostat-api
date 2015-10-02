package org.fao.faostat.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class DefaultOptionsBean {

    private String datasource;

    private String apiKey;

    private String clientKey;

    private String outputType;

    private String[] blackList;

    private String[] whiteList;

    private Map<String, String> procedureParameters;

    public DefaultOptionsBean() {
        this.setDatasource("faostat");
        this.setApiKey(null);
        this.setClientKey(null);
        this.setOutputType("objects");
        this.setProcedureParameters(new HashMap<String, String>());
    }

    public DefaultOptionsBean(String datasource, String apiKey, String clientKey, String outputType) {
        this.storeUserOptions(datasource, apiKey, clientKey, outputType);
    }

    public void storeUserOptions(String datasource, String apiKey, String clientKey, String outputType) {
        this.setDatasource(datasource != null ? datasource : this.getDatasource());
        this.setApiKey(apiKey != null ? apiKey : this.getApiKey());
        this.setClientKey(clientKey != null ? clientKey : this.getClientKey());
        this.setOutputType(outputType != null ? outputType : this.getOutputType());
        this.setProcedureParameters(new HashMap<String, String>());
    }

    public String[] getBlackList() {
        return blackList;
    }

    public void setBlackList(String[] blackList) {
        this.blackList = blackList;
    }

    public String[] getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String[] whiteList) {
        this.whiteList = whiteList;
    }

    public void addParameter(String key, String value) {
        this.getProcedureParameters().put(key, value);
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

    public Map<String, String> getProcedureParameters() {
        return procedureParameters;
    }

    public void setProcedureParameters(Map<String, String> procedureParameters) {
        this.procedureParameters = procedureParameters;
    }

    @Override
    public String toString() {
        return "DefaultOptionsBean{" +
                "datasource='" + datasource + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", clientKey='" + clientKey + '\'' +
                ", outputType='" + outputType + '\'' +
                ", procedureParameters=" + procedureParameters +
                '}';
    }

}
