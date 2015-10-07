package org.fao.faostat.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class DefaultOptionsBean {

    private String datasource;

    private String apiKey;

    private String clientKey;

    private String outputType;

    private List<String> blackList;

    private List<String> whiteList;

    private Map<String, Object> procedureParameters;

    private List<Map<String, Object>> dsd;

    public DefaultOptionsBean() {
        this.setDatasource("faostat");
        this.setApiKey(null);
        this.setClientKey(null);
        this.setOutputType("objects");
        this.setProcedureParameters(new HashMap<String, Object>());
        this.setDsd(new ArrayList<Map<String, Object>>());
    }

    public DefaultOptionsBean(String datasource, String apiKey, String clientKey, String outputType) {
        this.storeUserOptions(datasource, apiKey, clientKey, outputType);
    }

    public void storeUserOptions(String datasource, String apiKey, String clientKey, String outputType) {
        this.setDatasource(datasource != null ? datasource : this.getDatasource());
        this.setApiKey(apiKey != null ? apiKey : this.getApiKey());
        this.setClientKey(clientKey != null ? clientKey : this.getClientKey());
        this.setOutputType(outputType != null ? outputType : this.getOutputType());
        this.setProcedureParameters(new HashMap<String, Object>());
        this.setDsd(new ArrayList<Map<String, Object>>());
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public void addParameter(String key, Object value) {
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

    public Map<String, Object> getProcedureParameters() {
        return procedureParameters;
    }

    public void setProcedureParameters(Map<String, Object> procedureParameters) {
        this.procedureParameters = procedureParameters;
    }

    public List<Map<String, Object>> getDsd() {
        return dsd;
    }

    public void setDsd(List<Map<String, Object>> dsd) {
        this.dsd = dsd;
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
