package com.mnt.protocol.init;

import com.mnt.base.classloader.ClassLoadUtil;
import com.mnt.gui.fx.init.InitContext;
import com.mnt.protocol.core.TemplateClassLoad;
import com.mnt.protocol.model.UserData;
import lombok.extern.slf4j.Slf4j;

import java.net.URLClassLoader;
import java.util.List;

/**
 * 启动初始化
 *
 * @author jiangbiao
 * @Date 2017年8月8日下午3:05:04
 */
@Slf4j
public class StartInit extends InitContext {

	
	@Override
	public void afterInitView(URLClassLoader classLoad) {




	}

	@Override
	public void init(List<Class<?>> classes, URLClassLoader classLoad) {

		try {
			TemplateClassLoad.init(classes, classLoad);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("启动失败", e);
		}

		//初始化用户数据
		UserData.init();
		
	}

	@Override
	public void shutdown() {
		try {
			TemplateClassLoad.unloadClass();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
