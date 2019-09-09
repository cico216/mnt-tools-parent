package com.mnt.protocol.model;

import lombok.Data;

import java.util.Map;

/**
 * 用户属性配置
 */
@Data
public class UserConfig {

    /**
     * 最后一次选择的文件夹
     */
    private String lastSelectedDir;

    /**
     * 项目路径
     */
    private String projectPath;

    /**
     * 当前用户
     */
    private String user;

    /**
     * 请求头参数
     */
    private Map<String , String> headers;

    /**
     * 生成代码类型
     */
    private String generateCodeType;

    /**
     * 是否生成验证代码
     */
    private Boolean generateValid = false;

}
