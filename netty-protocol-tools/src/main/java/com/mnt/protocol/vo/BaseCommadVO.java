package com.mnt.protocol.vo;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.dom4j.Node;

/**
 * 基础命令vo
 */
public class BaseCommadVO {

    /**
     * 是否选择
     */
    private SimpleBooleanProperty choose = new SimpleBooleanProperty(false);

    /**
     * 命令名称
     */
    private SimpleStringProperty remark = new SimpleStringProperty("");

    /**
     * 命令路径
     */
    private SimpleStringProperty path = new SimpleStringProperty("");


    /**
     * 请求方法类型
     */
    private SimpleStringProperty method = new SimpleStringProperty("");


    /**
     * 请求方法类型
     */
    private SimpleBooleanProperty body = new SimpleBooleanProperty(false);

    /**
     * 当前节点
     */
    private Node currNode;


    public boolean isChoose() {
        return choose.get();
    }

    public SimpleBooleanProperty chooseProperty() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose.set(choose);
    }

    public String getRemark() {
        return remark.get();
    }

    public SimpleStringProperty remarkProperty() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark.set(remark);
    }

    public Node getCurrNode() {
        return currNode;
    }

    public void setCurrNode(Node currNode) {
        this.currNode = currNode;
    }

    public String getPath() {
        return path.get();
    }

    public SimpleStringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public String getMethod() {
        return method.get();
    }

    public SimpleStringProperty methodProperty() {
        return method;
    }

    public void setMethod(String method) {
        this.method.set(method);
    }

    public boolean isBody() {
        return body.get();
    }

    public SimpleBooleanProperty bodyProperty() {
        return body;
    }

    public void setBody(boolean body) {
        this.body.set(body);
    }
}
