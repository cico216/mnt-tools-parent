package com.mnt.protocol.model;

import java.util.List;

/**
 * proto model obj
 * @author cico
 */
public class ProtoModel {

    /**
     * 代码生成配置信息
     */
    private GenerateConfigInfo generateConfigInfo;


    /**
     * 导入的包列表
     */
    private List<String> importPackages;

    /**
     * 控制层名称
     */
    private String controllerName;

    /**
     * 请求路径
     */
    private String requestMapper;

    /**
     * 注释信息
     */
    private String remark;

    /**
     * 当前用户
     */
    private String user;

    /**
     * 创建日期
     */
    private String date;

    /**
     * 请求列表
     */
    private List<ActionModel> actions;


    public List<String> getImportPackages() {
        return importPackages;
    }

    public void setImportPackages(List<String> importPackages) {
        this.importPackages = importPackages;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ActionModel> getActions() {
        return actions;
    }

    public void setActions(List<ActionModel> actions) {
        this.actions = actions;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getRequestMapper() {
        return requestMapper;
    }

    public void setRequestMapper(String requestMapper) {
        this.requestMapper = requestMapper;
    }

    public GenerateConfigInfo getGenerateConfigInfo() {
        return generateConfigInfo;
    }

    public void setGenerateConfigInfo(GenerateConfigInfo generateConfigInfo) {
        this.generateConfigInfo = generateConfigInfo;
    }
}
