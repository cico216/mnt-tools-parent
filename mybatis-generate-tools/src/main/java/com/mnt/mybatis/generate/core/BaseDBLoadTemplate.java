package com.mnt.mybatis.generate.core;

import com.mnt.base.classloader.anno.ClassKey;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import com.mnt.mybatis.generate.vo.TableColumnVO;
import com.mnt.mybatis.generate.vo.TableNameVO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * base db column and table info Template
 * @author cico
 */
public abstract class BaseDBLoadTemplate {

    public String getKey() {
        return getJdbcInfo().getDbType();
    }

    /**
     * 获取驱动
     * @return
     */
    public String getDriver() {return getJdbcInfo().getDbDriver(); }

    /**
     * 获取jdbc连接信息
     * @return
     */
    protected abstract JDBCInfo getJdbcInfo();


    private static final Logger log = LoggerFactory.getLogger(BaseDBLoadTemplate.class);

    /**
     * 获取jdbc连接
     * @param jdbcInfo jdbc信息
     * @return jdbc 连接
     */
    public Connection getConnection(JDBCInfo jdbcInfo) {
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

    /**
     * 获取所有表名
     * @return
     */
    public ObservableList<TableNameVO> listTableName() {
        final ObservableList<TableNameVO> result = FXCollections.observableArrayList();

        result.addAll(listTableNameImpl());

        return result;
    }

    /**
     * 获取所有表名实现
     * @return
     */
    protected abstract List<TableNameVO> listTableNameImpl();

    /**
     * 获取表的列明
     * @param tableName 指定的表
     * @return
     */
    public ObservableList<TableColumnVO> listTableColumn(String tableName) {
        final ObservableList<TableColumnVO> result = FXCollections.observableArrayList();

        result.addAll(listTableColumnImpl(tableName));

        return result;
    }

    /**
     * 获取表的所有字段
     * @return
     */
    protected abstract List<TableColumnVO> listTableColumnImpl(String tableName);

}
