package com.mnt.mybatis.generate.model.db;

import java.util.List;

/**
 * 数据库字段模型
 *
 * @author jiangbiao
 * @Date 2017年8月8日下午3:12:49
 */
public class DBModel {
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 数据库表字段
	 */
	private List<DBCloumn> dbCloumns;
	

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<DBCloumn> getDbCloumns() {
		return dbCloumns;
	}

	public void setDbCloumns(List<DBCloumn> dbCloumns) {
		this.dbCloumns = dbCloumns;
	}
	
}
