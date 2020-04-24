package scripts.generates;


import com.mnt.common.utils.GenerateNameUtils;
import com.mnt.common.utils.TimeUtils;
import com.mnt.common.utils.VelocityUtils;
import com.mnt.mybatis.generate.core.BaseCodeGenerateTemplate;
import com.mnt.mybatis.generate.model.db.DBCloumn;
import com.mnt.mybatis.generate.model.db.DBModel;
import com.mnt.mybatis.generate.model.generate.CodeGenerateInfo;
import com.mnt.mybatis.generate.model.generate.GenerateConfig;
import com.mnt.mybatis.generate.model.view.PropertyType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mysql 代码生成
 */
public class MysqlCodeGenerate extends BaseCodeGenerateTemplate {

    /**
     * 需要过滤的字段
     */
    private List<String> filterColumns = new ArrayList<>();


    /**
     * 需要引包的类型
     */
    private Map<String, String> importColumns = new HashMap<>();


    public MysqlCodeGenerate() {
        filterColumns.add("user_create");
        filterColumns.add("user_modified");
        filterColumns.add("gmt_create");
        filterColumns.add("gmt_modified");
        filterColumns.add("del_flag");
        filterColumns.add("company_flag");
        filterColumns.add("remark");

        importColumns.put("Date", "java.util.Date");
        importColumns.put("BigDecimal", "java.math.BigDecimal");
    }


    @Override
    public String getDBType() {
        return "mysql";
    }

    @Override
    public List<CodeGenerateInfo> loadPropertieskey() {
        List<CodeGenerateInfo>  result = new ArrayList<>();
        result.add(new CodeGenerateInfo("代码作者","project.author", "cico", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("项目路径","project.path", "", PropertyType.DIRECTOR));
        result.add(new CodeGenerateInfo("dao路径","project.dao.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("mapper路径","project.mapper.path", "", PropertyType.TEXT));
//        result.add(new CodeGenerateInfo("vo路径","project.vo.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("query路径","project.query.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("entity路径","project.entity.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("service路径","project.service.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("serviceImpl路径","project.serviceimpl.path", "", PropertyType.TEXT));

        return result;
    }

    @Override
    protected void generateImpl(DBModel dbModel, GenerateConfig generateConfig) {
        //基本文件名
        String baseFileName = GenerateNameUtils.getClassFileName(dbModel.getTableName());

        Map<String, String> propertiesMap = generateConfig.getProperties();
        String projectAuthor = propertiesMap.get("project.author");
        String projectPath = propertiesMap.get("project.path");
        String entityPath = propertiesMap.get("project.entity.path");
        String queryPath = propertiesMap.get("project.query.path");


        //基础实体的依赖类
        String entityPackagePath = handlePackage(entityPath) + "." + baseFileName;
        String queryPackagePath = handlePackage(queryPath) + "." + baseFileName + "Query";

        //公共参数map
        Map<String, String> baseParams = new HashMap<>(6);
        baseParams.put("date", TimeUtils.formatDate(null, null));
        baseParams.put("user", projectAuthor);
        baseParams.put("remark", dbModel.getRemark());
        baseParams.put("entityName", baseFileName);
        baseParams.put("entityPackagePath", entityPackagePath);
        baseParams.put("queryPackage", queryPackagePath);

        //基础模板路径
        String bastTmpPath = getDBType() + "/";

        try {
            generateDao(projectPath, baseFileName, dbModel, propertiesMap.get("project.dao.path"),  bastTmpPath + "dao.vm", baseParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            generateMapper(projectPath, baseFileName, dbModel, propertiesMap.get("project.mapper.path"),  bastTmpPath + "mapper.vm", baseParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            generateVO(projectPath, baseFileName, dbModel, propertiesMap.get("project.vo.path"),  bastTmpPath + "vo.vm", baseParams);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            generateQuery(projectPath, baseFileName, dbModel, propertiesMap.get("project.query.path"),  bastTmpPath + "query.vm", baseParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            generateEntity(projectPath, baseFileName, dbModel, propertiesMap.get("project.entity.path"),  bastTmpPath + "entity.vm", baseParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            generateService(projectPath, baseFileName, dbModel, propertiesMap.get("project.service.path"),  bastTmpPath + "service.vm", baseParams);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            String daoPath = propertiesMap.get("project.dao.path");
            String servicePath = propertiesMap.get("project.service.path");
            String daoPackagePath = handlePackage(daoPath);
            String servicePackagePath = handlePackage(servicePath);

            generateServiceImpl(projectPath, baseFileName, daoPackagePath , servicePackagePath, dbModel, propertiesMap.get("project.serviceimpl.path"),  bastTmpPath + "serviceImpl.vm", baseParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 生成基础的ServiceImpl文件
     * @param projectPath 项目路径
     * @param baseFileName 基础的文件名
     * @param dbModel 数据库模型
     * @param propertiesPath 当前属性路径
     * @param tmpName 模板名称
     * @param baseParams 基础参数
     */
    private void generateServiceImpl(String projectPath, String baseFileName, String daoPackagePath, String servicePackagePath, DBModel dbModel, String propertiesPath, String tmpName, Map<String, String> baseParams) {
        String filePath = buildFileBaseName(projectPath, propertiesPath);

        //类名
        String className = baseFileName + "ServiceImpl";
        //对应的接口名称
        String serviceClassName = "I" + baseFileName + "Service";
        //生成文件路径
        String generateFilePath = filePath + className + ".java";

        String queryEntity = baseFileName + "Query";

        //对应的dao名称
        String daoClassName = baseFileName + "Mapper";
        String daoJavaName = GenerateNameUtils.getJavaName(dbModel.getTableName()) + "Mapper";

        List<DBCloumn> dbCloumns = new ArrayList<>();
        for (DBCloumn dbCloumn : dbModel.getDbCloumns()) {
            if(!filterColumns.contains(dbCloumn.getCloumnName())) {
                dbCloumns.add(dbCloumn);
            }
        }

        //写入参数
        Map<String, Object> tmpParams = new HashMap<>();
        tmpParams.putAll(baseParams);

        tmpParams.put("dbCloumns", dbCloumns);
        tmpParams.put("className", className);
        tmpParams.put("package", handlePackage(propertiesPath));
        tmpParams.put("queryEntity", queryEntity);

        tmpParams.put("serviceClassName", serviceClassName);
        tmpParams.put("daoClassName", daoClassName);
        tmpParams.put("daoJavaName", daoJavaName);
        tmpParams.put("daoPackagePath", daoPackagePath + "." + daoClassName);
        tmpParams.put("servicePackagePath", servicePackagePath + "." + serviceClassName);

        tmpParams.put("holdCode", getHoldCode(generateFilePath)); // 保留代码

        VelocityUtils.getInstance().parseTemplate(tmpName, generateFilePath, tmpParams);

    }


    /**
     * 生成基础的Service文件
     * @param projectPath 项目路径
     * @param baseFileName 基础的文件名
     * @param dbModel 数据库模型
     * @param propertiesPath 当前属性路径
     * @param tmpName 模板名称
     * @param baseParams 基础参数
     */
    private void generateService(String projectPath, String baseFileName, DBModel dbModel, String propertiesPath, String tmpName, Map<String, String> baseParams) {
        String filePath = buildFileBaseName(projectPath, propertiesPath);

        //类名
        String className = "I" + baseFileName + "Service";
        //生成文件路径
        String generateFilePath = filePath + className + ".java";

        String queryEntity = baseFileName + "Query";

        //写入参数
        Map<String, Object> tmpParams = new HashMap<>();
        tmpParams.putAll(baseParams);

        tmpParams.put("className", className);
        tmpParams.put("entityName", baseFileName);
        tmpParams.put("queryEntity", queryEntity);
        tmpParams.put("package", handlePackage(propertiesPath));


        tmpParams.put("holdCode", getHoldCode(generateFilePath)); // 保留代码

        VelocityUtils.getInstance().parseTemplate(tmpName, generateFilePath, tmpParams);

    }



    /**
     * 生成基础的Entity文件
     * @param projectPath 项目路径
     * @param baseFileName 基础的文件名
     * @param dbModel 数据库模型
     * @param propertiesPath 当前属性路径
     * @param tmpName 模板名称
     * @param baseParams 基础参数
     */
    private void generateEntity(String projectPath, String baseFileName, DBModel dbModel, String propertiesPath, String tmpName, Map<String, String> baseParams) {
        String filePath = buildFileBaseName(projectPath, propertiesPath);

        //类名
        String className = baseFileName;
        //生成文件路径
        String generateFilePath = filePath + className + ".java";

        //写入参数
        Map<String, Object> tmpParams = new HashMap<>();
        tmpParams.putAll(baseParams);

        tmpParams.put("package", handlePackage(propertiesPath));
        tmpParams.put("className", className);
        List<DBCloumn> dbCloumns = new ArrayList<>();
        List<String> importPackages = new ArrayList<>();

        for (DBCloumn dbCloumn : dbModel.getDbCloumns()) {
            if(!filterColumns.contains(dbCloumn.getCloumnName()) && !"id".equals(dbCloumn.getCloumnName())) {

                //判断是否需要引入包
                if(importColumns.containsKey(dbCloumn.getCloumnJavaType())) {
                    String packageName = importColumns.get(dbCloumn.getCloumnJavaType());
                    if(!importPackages.contains(packageName)) {
                        importPackages.add(packageName);
                    }
                }

                dbCloumns.add(dbCloumn);
            }
        }
        tmpParams.put("dbCloumns", dbCloumns);
        tmpParams.put("importPackages", importPackages);

        tmpParams.put("holdCode", getHoldCode(generateFilePath)); // 保留代码

        VelocityUtils.getInstance().parseTemplate(tmpName, generateFilePath, tmpParams);
    }

    /**
     * 生成基础的Query文件
     * @param projectPath 项目路径
     * @param baseFileName 基础的文件名
     * @param dbModel 数据库模型
     * @param propertiesPath 当前属性路径
     * @param tmpName 模板名称
     * @param baseParams 基础参数
     */
    private void generateQuery(String projectPath, String baseFileName, DBModel dbModel, String propertiesPath, String tmpName, Map<String, String> baseParams) {
        String filePath = buildFileBaseName(projectPath, propertiesPath);

        //类名
        String className = baseFileName + "Query";
        //生成文件路径
        String generateFilePath = filePath + className + ".java";

        //写入参数
        Map<String, Object> tmpParams = new HashMap<>();
        tmpParams.putAll(baseParams);

        tmpParams.put("package", handlePackage(propertiesPath));
        tmpParams.put("className", className);
        List<DBCloumn> dbCloumns = new ArrayList<>();
        List<String> importPackages = new ArrayList<>();

        for (DBCloumn dbCloumn : dbModel.getDbCloumns()) {
            if(!filterColumns.contains(dbCloumn.getCloumnName())) {

                //判断是否需要引入包
                if(importColumns.containsKey(dbCloumn.getCloumnJavaType())) {
                    String packageName = importColumns.get(dbCloumn.getCloumnJavaType());
                    if(!importPackages.contains(packageName)) {
                        importPackages.add(packageName);
                    }
                }

                dbCloumns.add(dbCloumn);
            }
        }
        tmpParams.put("dbCloumns", dbCloumns);
        tmpParams.put("importPackages", importPackages);

        tmpParams.put("holdCode", getHoldCode(generateFilePath)); // 保留代码

        VelocityUtils.getInstance().parseTemplate(tmpName, generateFilePath, tmpParams);
    }

    /**
     * 生成基础的VO文件
     * @param projectPath 项目路径
     * @param baseFileName 基础的文件名
     * @param dbModel 数据库模型
     * @param propertiesPath 当前属性路径
     * @param tmpName 模板名称
     * @param baseParams 基础参数
     */
    private void generateVO(String projectPath, String baseFileName, DBModel dbModel, String propertiesPath, String tmpName, Map<String, String> baseParams) {
        String filePath = buildFileBaseName(projectPath, propertiesPath);

        //类名
        String className = baseFileName + "VO";
        //生成文件路径
        String generateFilePath = filePath + className + ".java";

        //写入参数
        Map<String, Object> tmpParams = new HashMap<>();
        tmpParams.putAll(baseParams);

        tmpParams.put("package", handlePackage(propertiesPath));
        tmpParams.put("className", className);
        List<DBCloumn> dbCloumns = new ArrayList<>();
        List<String> importPackages = new ArrayList<>();

        for (DBCloumn dbCloumn : dbModel.getDbCloumns()) {
            if(!filterColumns.contains(dbCloumn.getCloumnName())) {

                //判断是否需要引入包
                if(importColumns.containsKey(dbCloumn.getCloumnJavaType())) {
                    String packageName = importColumns.get(dbCloumn.getCloumnJavaType());
                    if(!importPackages.contains(packageName)) {
                        importPackages.add(packageName);
                    }
                }

                dbCloumns.add(dbCloumn);
            }
        }
        tmpParams.put("dbCloumns", dbCloumns);
        tmpParams.put("importPackages", importPackages);


        tmpParams.put("holdCode", getHoldCode(generateFilePath)); // 保留代码

        VelocityUtils.getInstance().parseTemplate(tmpName, generateFilePath, tmpParams);
    }

    /**
     *  生成dao
     * @param projectPath
     * @param dbModel
     * @param propertiesPath
     */
    private void generateDao(String projectPath, String baseFileName, DBModel dbModel, String propertiesPath, String tmpName, Map<String, String> baseParams) {
        String filePath =buildFileBaseName(projectPath, propertiesPath);
        String className = baseFileName + "Mapper";

        //生成的文件名
        String generateFilePath = filePath + className + ".java";

        //写入参数
        Map<String, Object> tmpParams = new HashMap<>();
        tmpParams.putAll(baseParams);

        tmpParams.put("className", className);
        tmpParams.put("package", handlePackage(propertiesPath));


        tmpParams.put("holdCode", getHoldCode(generateFilePath)); // 保留代码

        VelocityUtils.getInstance().parseTemplate(tmpName, generateFilePath, tmpParams);
    }

    /**
     * 生成基础的mapper文件
     * @param projectPath 项目路径
     * @param baseFileName 基础的文件名
     * @param dbModel 数据库模型
     * @param propertiesPath 当前属性路径
     * @param tmpName 模板名称
     * @param baseParams 基础参数
     */
    private void generateMapper(String projectPath, String baseFileName, DBModel dbModel, String propertiesPath, String tmpName, Map<String, String> baseParams) {
        String filePath = buildFileBaseName(projectPath, propertiesPath);

        String mapperName = baseFileName + "Mapper";

        //生成文件路径
        String generateFilePath = filePath + mapperName + ".xml";

        //写入参数
        Map<String, Object> tmpParams = new HashMap<>();
        tmpParams.putAll(baseParams);

        tmpParams.put("tableName", dbModel.getTableName());
        tmpParams.put("mapperName", mapperName);
        tmpParams.put("package", handlePackage(propertiesPath));
        tmpParams.put("dbCloumns", dbModel.getDbCloumns());

        tmpParams.put("holdCode", getHoldCode(generateFilePath)); // 保留代码

        VelocityUtils.getInstance().parseTemplate(tmpName, generateFilePath, tmpParams);
    }














    private String buildFileBaseName(String projectPath, String propertiesPath) {
        String filePath = handlerPath(projectPath) + "/" + handlerPath(propertiesPath);
        if(!filePath.endsWith("/")) {
            filePath += "/";
        }

        checkAndCreateDir(filePath);
        return filePath;
    }

    /**
     * 处理路径问题
     * @param path
     * @return
     */
    private String handlerPath(String path) {
        return path.replaceAll("\\\\", "/").replaceAll("\\.", "/");
    }

    /**
     * 获取路径下的包名
     * @param path
     * @return
     */
    private String handlePackage(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

}
