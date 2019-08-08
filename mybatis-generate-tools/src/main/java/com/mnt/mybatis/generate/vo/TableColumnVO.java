package com.mnt.mybatis.generate.vo;

import com.mnt.gui.fx.table.anno.FXColumn;
import com.mnt.gui.fx.table.anno.FXTable;
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
@FXTable
public class TableColumnVO {

	@FXColumn(idName = "tcolCloumnName")
	private String cloumnName; //字段名
	@FXColumn(idName = "tcolLength")
	private int length; //字段长度
	@FXColumn(idName = "tcolCloumnType")
	private String cloumnType;//字段类型
	@FXColumn(idName = "tcolRemark")
	private String remark; //备注


	public String getCloumnName() {
		return cloumnName;
	}

	public void setCloumnName(String cloumnName) {
		this.cloumnName = cloumnName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getCloumnType() {
		return cloumnType;
	}

	public void setCloumnType(String cloumnType) {
		this.cloumnType = cloumnType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "TableColumnVO [cloumnName=" + getCloumnName() + ", length=" + getLength() + ", cloumnType=" + getCloumnType()
				+ ", remark=" + getRemark() + "]";
	}
	
	
	
}
