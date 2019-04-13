package com.mnt.mybatis.generate.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 表格列 vo
 *
 * @author jiangbiao
 * @Date 2017年5月12日下午1:18:14
 */
public class TableColumnVO {

	private StringProperty cloumnName = new SimpleStringProperty(); //字段名
	private IntegerProperty length = new SimpleIntegerProperty(); //字段长度
	private StringProperty cloumnType = new SimpleStringProperty();//字段类型
	private StringProperty remark = new SimpleStringProperty(); //备注
	
	public String getCloumnName() {
		return cloumnName.get();
	}
	public void setCloumnName(String cloumnName) {
		this.cloumnName.set(cloumnName);
	}
	
	public StringProperty cloumnNameProperty()
	{
		return cloumnName;
	}
	
	public Integer getLength() {
		return length.get();
	}
	public void setLength(Integer length) {
		this.length.set(length);;
	}
	
	public IntegerProperty lengthProperty()
	{
		return length;
	}
	
	public String getCloumnType() {
		return cloumnType.get();
	}
	public void setCloumnType(String cloumnType) {
		this.cloumnType.set(cloumnType);
	}
	
	public StringProperty cloumnTypeProperty()
	{
		return cloumnType;
	}
	
	public String getRemark() {
		return remark.get();
	}
	public void setRemark(String remark) {
		this.remark.set(remark);
	}
	
	public StringProperty remarkProperty()
	{
		return remark;
	}
	
	@Override
	public String toString() {
		return "TableColumnVO [cloumnName=" + getCloumnName() + ", length=" + getLength() + ", cloumnType=" + getCloumnType()
				+ ", remark=" + getRemark() + "]";
	}
	
	
	
}
