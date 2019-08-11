package com.mnt.mybatis.generate.model;


import com.mnt.common.utils.JSONConfigUtils;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import com.mnt.mybatis.generate.model.generate.GenerateConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录用户数据
 */
public class UserData {

    /**
     * 用户基础配置表
     */
    private static UserConfig userConfig;

    /**
     * jdbc配置信息列表
     */
    private static List<JDBCInfo> jdbcInfos;

    /**
     * 代码生成配置列表
     */
    private static List<GenerateConfig> generateConfigs;

    /**
     * 初始化用户数据
     */
    public static void init() {
        userConfig = JSONConfigUtils.loadData(UserConfig.class, getBasePath() + CONFIG_PATH);
        if(null == userConfig) {
            userConfig = new UserConfig();
        }

        jdbcInfos = JSONConfigUtils.loadDatas(JDBCInfo.class, getBasePath() + DB_CONFIG_PATH);
        if(null == jdbcInfos) {
            jdbcInfos = new ArrayList<>();
        }

        generateConfigs = JSONConfigUtils.loadDatas(GenerateConfig.class, getBasePath() + GENERATE_CONFIG_PATH);
        if(null == generateConfigs) {
            generateConfigs = new ArrayList<>();
        }

    }

    /**
     * 配置文件地址
     */
    public static final String CONFIG_PATH = "conf/config.json";

    /**
     * db数据文件地址
     */
    public static final String DB_CONFIG_PATH = "conf/db-configs.json";

    /**
     * db数据文件地址
     */
    public static final String GENERATE_CONFIG_PATH = "conf/generate-configs.json";



    /**
     * 获取用户目录
     * @return
     */
    public static String getUserPath() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取用户配置数据
     * @return
     */
    public static UserConfig getUserConfig() {

        return userConfig;
    }

    /**
     * 保存用户数据
     */
    public static void saveUserConfig() {
        JSONConfigUtils.saveData(userConfig, getBasePath() + CONFIG_PATH);


    }


    private static String getBasePath() {
        return getUserPath() + "/";
    }

    /**
     * 获取所有db信息
     * @return
     */
    public static List<JDBCInfo> getJDBCInfos() {

        return jdbcInfos;
    }

    /**
     * 保存DB信息
     */
    public static void saveJDBCInfos() {
        JSONConfigUtils.saveData(jdbcInfos, getBasePath() + DB_CONFIG_PATH);
    }

    /**
     * 获取所有代码生成配置信息
     * @return
     */
    public static List<GenerateConfig> getGenerateConfigs() {

        return generateConfigs;
    }

    /**
     * 保存代码生成配置信息
     */
    public static void saveGenerateConfigs() {
        JSONConfigUtils.saveData(generateConfigs, getBasePath() + GENERATE_CONFIG_PATH);
    }



}
