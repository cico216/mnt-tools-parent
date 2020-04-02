package com.mnt.protocol.vo;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.dom4j.Node;

/**
 * 基础命令vo
 */
public class BaseCommandVO {

    /**
     * 是否选择
     */
    private SimpleBooleanProperty choose = new SimpleBooleanProperty(false);

    /**
     * 命令备注
     */
    private SimpleStringProperty remark = new SimpleStringProperty("");

    /**
     * 命令名称
     */
    private SimpleStringProperty name = new SimpleStringProperty("");

    /**
     * 命令编码
     */
    private SimpleIntegerProperty opCode = new SimpleIntegerProperty();

    /**
     * 命令来源
     */
    private SimpleStringProperty src = new SimpleStringProperty("");



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

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getOpCode() {
        return opCode.get();
    }

    public SimpleIntegerProperty opCodeProperty() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode.set(opCode);
    }

    public String getSrc() {
        return src.get();
    }

    public SimpleStringProperty srcProperty() {
        return src;
    }

    public void setSrc(String src) {
        this.src.set(src);
    }
}
