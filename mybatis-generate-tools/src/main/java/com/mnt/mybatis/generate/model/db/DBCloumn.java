package com.mnt.mybatis.generate.model.db;

/**
 * db表字段
 *
 * @author jiangbiao
 * @Date 2017年8月9日下午3:25:16
 */
public class DBCloumn {

	private String cloumnName ; //字段名
	private Integer length; //字段长度
	private String cloumnType;//字段类型
	private String remark; //字段备注
	
	private String cloumnJavaName; //获取字段的java命名
	
	private String cloumnJavaType; //获取字段的java类型
	
	private String cloumnJdbcType; //获取字段的jdbc类型
	
	private String methodName; //对应的方法名称
	
	
	
	public String getCloumnName() {
		return cloumnName;
	}
	public void setCloumnName(String cloumnName) {
		this.cloumnName = cloumnName;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
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
	public String getCloumnJavaName() {
		return cloumnJavaName;
	}
	public void setCloumnJavaName(String cloumnJavaName) {
		this.cloumnJavaName = cloumnJavaName;
	}
	public String getCloumnJavaType() {
		return cloumnJavaType;
	}
	public void setCloumnJavaType(String cloumnJavaType) {
		this.cloumnJavaType = cloumnJavaType;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getCloumnJdbcType() {
		return cloumnJdbcType;
	}
	public void setCloumnJdbcType(String cloumnJdbcType) {
		this.cloumnJdbcType = cloumnJdbcType;
	}
	
}
