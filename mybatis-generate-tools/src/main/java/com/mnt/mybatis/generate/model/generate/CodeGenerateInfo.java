package com.mnt.mybatis.generate.model.generate;

import com.mnt.mybatis.generate.model.view.PropertyType;
import lombok.Data;

/**
 * 代码生成属性信息
 */
@Data
public class CodeGenerateInfo {

    public CodeGenerateInfo(String propertyName, String propertyKey, String propertyValue, PropertyType type) {
        this.propertyName = propertyName;
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
        this.type = type;
    }

    /**
     * 属性名
     */
    private String propertyName;

    /**
     * 属性键值
     */
    private String propertyKey;

    /**
     * 属性值
     */
    private String propertyValue;

    /**
     * 属性设置类型
     */
    private PropertyType type;


}
