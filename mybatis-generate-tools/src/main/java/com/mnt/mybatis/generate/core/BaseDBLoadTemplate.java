package com.mnt.mybatis.generate.core;

import com.mnt.mybatis.generate.model.db.JDBCInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * base db column and table info Template
 * @author cico
 */
public abstract class BaseDBLoadTemplate {

    private static final Logger log = LoggerFactory.getLogger(BaseDBLoadTemplate.class);

    /**
     * 获取jdbc连接
     * @param jdbcInfo jdbc信息
     * @return jdbc 连接
     */
    protected Connection getConnection(JDBCInfo jdbcInfo) {
        try {
            Class.forName(jdbcInfo.getDbDriver());
            Connection connection = DriverManager.getConnection(jdbcInfo.getDbUrl(), jdbcInfo.getDbUserName(), jdbcInfo.getDbPassword());
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            log.error("数据库连接失败 ", e);
        }
        return null;
    }

    /**
     * 关闭数据库链接
     * @param connection
     */
    protected void closeConnection(Connection connection) {
        try {
            if(null != connection) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("连接关闭失败 ", e);
        }
    }

}
