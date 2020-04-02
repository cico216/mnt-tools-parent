package com.mnt.protocol.model;

import lombok.Data;

import java.util.List;

/**
 * proto model obj
 * @author cico
 */
@Data
public class ProtoModel {

    /**
     * 代码生成配置信息
     */
    private GenerateConfigInfo generateConfigInfo;


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
    private List<CommandModel> commands;


}
