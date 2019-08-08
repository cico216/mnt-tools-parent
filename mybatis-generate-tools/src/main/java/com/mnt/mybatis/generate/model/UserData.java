package com.mnt.mybatis.generate.model;


import com.mnt.common.utils.JSONConfigUtils;
import com.mnt.mybatis.generate.model.db.JDBCInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录用户数据
 */
public class UserData {

    private static UserConfig userConfig;

    private static List<JDBCInfo> jdbcInfos;



    /**
     * 初始化用户数据
     */
    public static void init() {
        userConfig = JSONConfigUtils.loadData(UserConfig.class, getBasePath() + CONFIG_PATH);
        if(null == userConfig) {
            userConfig = new UserConfig();
        }

        jdbcInfos = JSONConfigUtils.loadDatas(JDBCInfo.class, getBasePath() + DATA_PATH);
        if(null == jdbcInfos) {
            jdbcInfos = new ArrayList<>();
        }

    }

    /**
     * 配置文件地址
     */
    public static final String CONFIG_PATH = "conf/config.json";

    /**
     * 数据文件地址
     */
    public static final String DATA_PATH = "conf/data.json";



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
     *
     * @return
     */
    public static List<JDBCInfo> getJDBCInfos() {

        return jdbcInfos;
    }

    public static void saveJDBCInfos() {
        JSONConfigUtils.saveData(jdbcInfos, getBasePath() + DATA_PATH);
    }




}
