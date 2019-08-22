package com.mnt.protocol.core;

import com.mnt.base.classloader.ClassLoadSupport;
import com.mnt.base.classloader.anno.ClassLoad;


/**
 * 代码生成脚本加载类
 *
 * @author jiangbiao
 * @Date 2018年5月4日下午3:03:50
 */
public class TemplateClassLoad {

	@ClassLoad(srcPath="scripts/generate")
	public static ClassLoadSupport<ProtoCodeGenerateTemplate> PROTO_CODE_GENERATE_TEMPLATE;

}
