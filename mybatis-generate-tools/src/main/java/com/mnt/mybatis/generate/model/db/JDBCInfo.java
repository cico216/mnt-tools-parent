package com.mnt.mybatis.generate.model.db;

/**
 * jdbc 信息
 * @author cico
 */
public class JDBCInfo {

    /**
     * db类型
     */
    private String dbType;

    /**
     * db link url
     */
    private String dbUrl;

    /**
     * db driver
     */
    private String dbDriver;

    /**
     * db login name
     */
    private String dbUserName;

    /**
     * db pwd
     */
    private String dbPassword;

    /**
     * 当前配置命名
     */
    private String configName;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }
}
