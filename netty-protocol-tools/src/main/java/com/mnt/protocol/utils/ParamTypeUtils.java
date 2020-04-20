package com.mnt.protocol.utils;

/**
 * 参数类型工具类
 * @author jiangbiao
 * @date 2018/9/3 17:04
 */
public class ParamTypeUtils {


    /**
     * 参数类型转换
     * @param typeName
     * @return
     */
    public static String convertType(String typeName) {
        if("String".equals(typeName) || "string".equals(typeName) || "STRING".equals(typeName) || "java.lang.String".equals(typeName)) {
            return "String";
        } else if("Long".equals(typeName) || "long".equals(typeName)) {
            return "Long";
        } else if("int".equals(typeName) || "Integer".equals(typeName) || "integer".equals(typeName) || "INTEGER".equals(typeName) || "java.lang.Integer".equals(typeName)) {
            return "Integer";
        } else if("boolean".equals(typeName) || "bool".equals(typeName) || "Boolean".equals(typeName) || "BOOLEAN".equals(typeName) || "java.lang.Boolean".equals(typeName)) {
            return "Boolean";
        } else if("float".equals(typeName) || "Float".equals(typeName) || "FLOAT".equals(typeName) || "java.lang.Float".equals(typeName)) {
            return "Float";
        } else if("double".equals(typeName) || "Double".equals(typeName) || "DOUBLE".equals(typeName) || "java.lang.Double".equals(typeName)) {
            return "Double";
        }  else if("date".equals(typeName) || "Date".equals(typeName) || "DATE".equals(typeName) || "java.util.Date".equals(typeName)) {
            return "Date";
        }  else if("char".equals(typeName) || "CHAR".equals(typeName) || "Character".equals(typeName) || "character".equals(typeName) || "CHARACTER".equals(typeName) || "java.lang.Character".equals(typeName)) {
            return "String";
        }  else if("byte".equals(typeName) || "Byte".equals(typeName) || "BYTE".equals(typeName) || "java.lang.Byte".equals(typeName)) {
            return "String";
        }  else if("short".equals(typeName) || "Short".equals(typeName) || "SHORT".equals(typeName) || "java.lang.Short".equals(typeName)) {
            return "Integer";
        }  else if("BigDecimal".equals(typeName) || "bigdecimal".equals(typeName) || "BIGDECIMAL".equals(typeName) || "java.math.BigDecimal".equals(typeName)) {
            return "BigDecimal";
        }  else if("list".equals(typeName) || "List".equals(typeName) || "LIST".equals(typeName) || "java.util.List".equals(typeName)) {
            return "List";
        }

        return null;
    }

    /**
     * 获取拆箱类型
     * @param typeName 类型名称
     * @return 拆箱类型
     */
    public static String getUnboxType(String typeName) {
        if("String".equals(typeName)) {
            return "String";
        } else if("Long".equals(typeName)) {
            return "long";
        } else if("Integer".equals(typeName)) {
            return "int";
        } else if("Boolean".equals(typeName)) {
            return "boolean";
        } else if("Float".equals(typeName)) {
            return "float";
        } else if("Double".equals(typeName)) {
            return "double";
        }  else if("Date".equals(typeName)) {
            return "Date";
        }  else if("List".equals(typeName)) {
            return "List";
        }
        return null;
    }

}
