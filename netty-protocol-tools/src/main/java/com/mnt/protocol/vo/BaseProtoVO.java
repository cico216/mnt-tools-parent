package com.mnt.protocol.vo;

import com.mnt.protocol.utils.XMLParseUtils;
import javafx.beans.property.SimpleStringProperty;

/**
 * 基础协议列表vo
 */
public class BaseProtoVO {

    private SimpleStringProperty remark = new SimpleStringProperty("");

    private SimpleStringProperty filePath = new SimpleStringProperty("");

    /**
     * 命令号范围
     */
    private SimpleStringProperty codeLimit = new SimpleStringProperty("");


    /**
     * 模块名称
     */
    private SimpleStringProperty moduleName = new SimpleStringProperty("");

    private boolean isDir;

    private XMLParseUtils.XMLObject xmlObject;

    public String getRemark() {
        return remark.get();
    }

    public SimpleStringProperty remarkProperty() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark.set(remark);
    }

    public XMLParseUtils.XMLObject getXmlObject() {
        return xmlObject;
    }

    public void setXmlObject(XMLParseUtils.XMLObject xmlObject) {
        this.xmlObject = xmlObject;
    }

    public String getFilePath() {
        return filePath.get();
    }

    public SimpleStringProperty filePathProperty() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getCodeLimit() {
        return codeLimit.get();
    }

    public SimpleStringProperty codeLimitProperty() {
        return codeLimit;
    }

    public void setCodeLimit(String path) {
        this.codeLimit.set(path);
    }

    public String getModuleName() {
        return moduleName.get();
    }

    public SimpleStringProperty moduleNameProperty() {
        return moduleName;
    }

    public void setModuleName(String method) {
        this.moduleName.set(method);
    }

    /**
     * 关闭文件流
     */
    public void clean() {
        if(null != xmlObject) {
            xmlObject.close();
        }
    }
}
