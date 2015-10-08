package org.fao.faostat.beans;

import org.fao.faostat.datasources.DATASOURCE;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class DatasourceBean {

    private String driver;

    private String url;

    private String dbName;

    private String username;

    private String password;

    public DatasourceBean(DATASOURCE datasource) {
        switch (datasource) {
            case TEST:
                this.setDbName("Warehouse");
                this.setDriver("SQLServer2000");
                this.setPassword("w@reh0use");
                this.setUrl("jdbc:sqlserver://HQWPRFAOSTATDB2\\\\Internal;databaseName=Warehouse;");
                this.setUsername("Warehouse");
                break;
            default:
                this.setDbName("Warehouse");
                this.setDriver("SQLServer2000");
                this.setPassword("w@reh0use");
                this.setUrl("jdbc:sqlserver://HQWPRFAOSTATDB2\\Internal;databaseName=Warehouse;");
                this.setUsername("Warehouse");
                break;
        }
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DatasourceBean{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", dbName='" + dbName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}