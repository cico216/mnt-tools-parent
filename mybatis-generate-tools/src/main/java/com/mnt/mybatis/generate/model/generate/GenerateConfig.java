package com.mnt.mybatis.generate.model.generate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Map;

/**
 * 代码生成配置
 */
public class GenerateConfig {

    /**
     * 是否使用当前配置
     */
    private BooleanProperty used = new SimpleBooleanProperty(false);

    /**
     * 配置名称
     */
    private String configName;

    /**
     * db类型
     */
    private String dbType;


    /**
     * 属性配置列表
     */
    private Map<String, String> properties;


    public boolean isUsed() {
        return used.get();
    }

    public BooleanProperty usedProperty() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used.set(used);
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }


    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
