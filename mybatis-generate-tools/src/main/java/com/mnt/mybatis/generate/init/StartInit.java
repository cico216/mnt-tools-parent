package com.mnt.mybatis.generate.init;

import com.mnt.base.classloader.ClassLoadUtil;
import com.mnt.gui.fx.init.InitContext;
import com.mnt.mybatis.generate.core.load.TemplateClassLoad;
import com.mnt.mybatis.generate.model.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URLClassLoader;
import java.util.List;

/**
 * 启动初始化
 *
 * @author jiangbiao
 * @Date 2017年8月8日下午3:05:04
 */
public class StartInit extends InitContext {

	private static final Logger log = LoggerFactory.getLogger(StartInit.class);
	
	@Override
	public void afterInitView(URLClassLoader classLoad) {

	
	}

	@Override
	public void init(List<Class<?>> classes, URLClassLoader classLoad) {
		try {
			//初始化动态编译脚本
			TemplateClassLoad.init(classes, classLoad);
//			ClassLoadUtil.loadClass(TemplateClassLoad.class, classLoad);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("加载模板脚本失败", e);
		}

		//初始化数据
		UserData.init();
		
	}

	@Override
	public void shutdown() {
		
	}
	
}
