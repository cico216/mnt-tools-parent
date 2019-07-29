package com.mnt.mybatis.generate.model.generate;

import com.mnt.mybatis.generate.model.view.PropertyType;
import lombok.Data;

/**
 * 代码生成属性信息
 */
@Data
public class CodeGenerateInfo {

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
