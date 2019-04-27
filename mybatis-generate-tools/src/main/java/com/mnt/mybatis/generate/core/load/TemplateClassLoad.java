package com.mnt.mybatis.generate.core.load;


import com.mnt.base.classloader.ClassLoadSupport;
import com.mnt.base.classloader.anno.ClassLoad;
import com.mnt.mybatis.generate.core.BaseCodeGenerateTemplate;
import com.mnt.mybatis.generate.core.BaseDBLoadTemplate;

/**
 * 模板脚本加载类
 *
 * @author jiangbiao
 * @Date 2017年8月8日下午3:03:50
 */
public class TemplateClassLoad {

	/**
	 * 数据库抽象脚本
	 */
	@ClassLoad(srcPath="scripts/dbtypes")
	public static ClassLoadSupport<BaseDBLoadTemplate> BASE_DB_INFO_LOAD_TEMPLATE;

	/**
	 * 数据库抽象脚本
	 */
	@ClassLoad(srcPath="scripts/generates")
	public static ClassLoadSupport<BaseCodeGenerateTemplate> BASE_CODE_GENERATE_TEMPLATE;


}
