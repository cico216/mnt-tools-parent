package com.mnt.mybatis.generate.model.db;

/**
 * jdbc 信息
 * @author cico
 */
public class JDBCInfo {
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
}
