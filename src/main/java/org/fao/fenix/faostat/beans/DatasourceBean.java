package org.fao.fenix.faostat.beans;

/**
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 * */
public class DatasourceBean {

    private String id;

    private String driver;

    private String url;

    private String dbName;

    private String username;

    private String password;

    private boolean create;

    private boolean retrieve = true;

    private boolean update;

    private boolean delete;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isRetrieve() {
        return retrieve;
    }

    public void setRetrieve(boolean retrieve) {
        this.retrieve = retrieve;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "DatasourceBean{" +
                "id='" + id + '\'' +
                ", driver=" + driver +
                ", url='" + url + '\'' +
                ", dbName='" + dbName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", create=" + create +
                ", retrieve=" + retrieve +
                ", update=" + update +
                ", delete=" + delete +
                '}';
    }

}