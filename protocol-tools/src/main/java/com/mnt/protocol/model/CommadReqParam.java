package com.mnt.protocol.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 每条请求参数
 * @author jiangbiao
 * @date 2018/8/17 14:58
 */
public class CommadReqParam {

    /**
     * 参数名
     */
    private String name;

    /**
     * 参数备注
     */
    private String remark;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数长度
     */
    private Integer length;


    /**
     * 是否必传
     */
    private Boolean must;

    /**
     * 类型泛型参数
     */
    private String typeClass;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * date日期格式
     */
    private String format;

    /**
     * 请求参数列表
     */
    private List<CommadReqParam> childrens = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Boolean getMust() {
        return must;
    }

    public void setMust(Boolean must) {
        this.must = must;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public List<CommadReqParam> getChildrens() {
        return childrens;
    }


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
