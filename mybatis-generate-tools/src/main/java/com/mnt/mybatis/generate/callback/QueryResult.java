package com.mnt.mybatis.generate.callback;

import java.sql.ResultSet;

/**
 * 查询结果回调
 *
 * @author jiangbiao
 * @Date 2017年5月12日下午1:32:05
 */
public interface QueryResult {

	/**
	 * 查询回调执行
	 * @param rs
	 */
	void calback(ResultSet rs);
	
}
