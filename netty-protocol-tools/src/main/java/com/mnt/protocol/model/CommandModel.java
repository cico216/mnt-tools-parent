package com.mnt.protocol.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * action名称
 * @author jiangbiao
 * @date 2018/8/17 12:08
 */
@Data
public class CommandModel {


    /**
     * 命令名称
     */
    private String name;

    /**
     * 命令号
     */
    private int opCode;

    /**
     * 发送来源
     */
    private String src;


    /**
     * 注释信息
     */
    private String remark;

    /**
     * 请求参数列表
     */
    private List<CommandParam> commandParams;


    /**
     * 内部请求参数
     */
    private Map<CommandParam, List<CommandParam>> innerParams;

    /**
     * 引入的包
     */
    private List<String> commandImportClass;

}
