package com.mnt.mybatis.generate.utils;

import com.mnt.mybatis.generate.callback.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * sql执行工具
 * @author jiangbiao
 * @Date 2017年5月12日上午11:53:43
 */
public class SqlExecuteUtils {

	private static final Logger log = LoggerFactory.getLogger(SqlExecuteUtils.class);

	/**
	 * 不带参数查询
	 * @param conn
	 * @param sql
	 * @param queryResult
	 */
	public static void query(Connection conn, String sql, QueryResult queryResult) {
		try (Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(sql)){
			queryResult.calback(rs);
		} catch (Exception e) {
			log.error("查询出错" , e);
			e.printStackTrace();

		}
	}

}
