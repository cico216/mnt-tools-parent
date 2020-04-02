package com.mnt.protocol.core;


import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


/**
 * 代码生成脚本加载类
 *
 * @author jiangbiao
 * @Date 2018年5月4日下午3:03:50
 */
public class TemplateClassLoad {

	/**
	 * 协议生成模板
	 */
	public static List<ProtoCodeGenerateTemplate> PROTO_CODE_GENERATE_TEMPLATE;


	/**
	 * 初始化类加载
	 * @param classes
	 * @param classLoad
	 */
	public static void init(List<Class<?>> classes, URLClassLoader classLoad) {
		PROTO_CODE_GENERATE_TEMPLATE = new ArrayList<>();
		for (Class<?> c : classes) {
			if(ProtoCodeGenerateTemplate.class.equals(c.getSuperclass())) {

				try {
					PROTO_CODE_GENERATE_TEMPLATE.add((ProtoCodeGenerateTemplate)c.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}
//		System.err.println(PROTO_CODE_GENERATE_TEMPLATE);



	}


	/**
	 * 卸载class
	 */
	public static void unloadClass() {
		PROTO_CODE_GENERATE_TEMPLATE.clear();
	}

}
