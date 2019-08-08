package com.mnt.mybatis.generate.core;

import com.mnt.mybatis.generate.model.db.DBModel;
import com.mnt.mybatis.generate.model.generate.CodeGenerateInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * 基础代码生成类
 * @author cico
 */
public abstract class BaseCodeGenerateTemplate {

    /**
     * 代码保留开始标记
     */
    protected static final String APPEND_START = "append__start";

    /**
     * 代码保留结束标记
     */
    protected static final String APPEND_END = "append__end";

    /**
     * 获取数据库类型
     * @return
     */
    public abstract String getDBType();

    /**
     * 获取代码生成属性信息
     * @return
     */
    protected abstract List<CodeGenerateInfo> loadPropertieskey();

    /**
     * 生成代码调用
     * @param dbModel
     */
    public final void generate(DBModel dbModel) {

        try {
            generateImpl(dbModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 执行生成代码方法
     * @param dbModel
     */
    protected abstract void generateImpl(DBModel dbModel);

    /**
     * 批量执行生成代码方法
     * @param dbModels
     */
    public final void generate(List<DBModel> dbModels) {
        for (DBModel dbModel : dbModels) {
            generate(dbModel);
        }
    }



    /**
     * 获取保留代码
     * @return
     */
    public String getHoldCode(String filePath) {
        File file = new File(filePath);
        StringBuilder sbResult = new StringBuilder();
        if(file.exists()) {
            try {
                boolean isStart = false;
                FileReader fr;

                fr = new FileReader(file);

                BufferedReader br=new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {

                    if(line.contains(APPEND_END)) {
                        break;
                    }

                    if(isStart) {
//		        		if(!line.trim().isEmpty()) {
                        sbResult.append(line + "\n");
//		        		}
                    }
                    if(line.contains(APPEND_START)) {
                        isStart = true;
                    }
                }
                br.close();
                fr.close();

                return sbResult.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "";
    }

    /**
     * 检测并创建文件夹
     */
    public void checkAndCreateDir(String filePath) {
        File file = new File(filePath);
        if(!file.exists()) {
            file.mkdirs();
        }
    }

}
