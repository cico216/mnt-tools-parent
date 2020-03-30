package com.mnt.mybatis.generate.core.load;


import com.mnt.mybatis.generate.core.BaseCodeGenerateTemplate;
import com.mnt.mybatis.generate.core.BaseDBLoadTemplate;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

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
	public static List<BaseDBLoadTemplate> BASE_DB_INFO_LOAD_TEMPLATE;

	/**
	 * 数据库抽象脚本
	 */
	public static List<BaseCodeGenerateTemplate> BASE_CODE_GENERATE_TEMPLATE;


	/**
	 * 初始化类加载
	 * @param classes
	 * @param classLoad
	 */
	public static void init(List<Class<?>> classes, URLClassLoader classLoad) {
		BASE_DB_INFO_LOAD_TEMPLATE = new ArrayList<>();
		BASE_CODE_GENERATE_TEMPLATE = new ArrayList<>();
		for (Class<?> c : classes) {
			if(BaseDBLoadTemplate.class.equals(c.getSuperclass())) {

				try {
					BASE_DB_INFO_LOAD_TEMPLATE.add((BaseDBLoadTemplate)c.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}

		for (Class<?> c : classes) {
			if(BaseCodeGenerateTemplate.class.equals(c.getSuperclass())) {

				try {
					BASE_CODE_GENERATE_TEMPLATE.add((BaseCodeGenerateTemplate)c.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}



	}


	/**
	 * 卸载class
	 */
	public static void unloadClass() {
		BASE_DB_INFO_LOAD_TEMPLATE.clear();
		BASE_CODE_GENERATE_TEMPLATE.clear();
	}

}
