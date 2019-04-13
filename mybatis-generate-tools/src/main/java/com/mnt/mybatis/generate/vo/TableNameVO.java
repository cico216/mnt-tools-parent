package com.mnt.mybatis.generate.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 表格名称vo
 *
 * @author jiangbiao
 * @Date 2017年5月12日下午1:09:34
 */
public class TableNameVO {

	private BooleanProperty check = new SimpleBooleanProperty(false); //是否选择
	private StringProperty tableName = new SimpleStringProperty(); //表名
	private StringProperty remark = new SimpleStringProperty(); //备注

	public String getTableName() {
		return tableName.get();
	}
	public void setTableName(String tableName) {
		this.tableName.setValue(tableName);
	}
	
	public BooleanProperty checkProperty()
	{
		return check;
	}
	
	public String getRemark() {
		return remark.get();
	}
	public void setRemark(String remark) {
		this.remark.set(remark);
	}
	
	public Boolean getCheck() {
		return check.get();
	}
	public void setCheck(Boolean check) {
		this.check.set(check);
	}
	@Override
	public String toString() {
		return "TableNameVO [tableName=" + getTableName() + ", remark=" + getRemark() + "]";
	}
	
	
	
}
