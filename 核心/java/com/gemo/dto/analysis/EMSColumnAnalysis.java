package com.gemo.dto.analysis;

import java.beans.PropertyDescriptor;

/**
 * EMS ORM解析Column
 */
public class EMSColumnAnalysis implements Comparable<EMSColumnAnalysis> {

	// propertyDescriptor
	private PropertyDescriptor propertyDescriptor;
	// 字段排序
	private Integer order;
	// 字段名
	private String name;
	// 字段类型
	private String type;
	// 字段长度
	private Integer length;
	// 字段精度（小数点后位数）
	private Integer scale;
	// 字段是否可为空
	private Boolean nullable;
	// 字段描述
	private String comment;
	// 是否以创建
	private Boolean created;
	// 主表
	private String mainEntity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public Boolean getCreated() {
		return created;
	}

	public void setCreated(Boolean created) {
		this.created = created;
	}

	public String getMainEntity() {
		return mainEntity;
	}

	public void setMainEntity(String mainEntity) {
		this.mainEntity = mainEntity;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
	}

	@Override
	public int compareTo(EMSColumnAnalysis another) {
		// TODO Auto-generated method stub
		return this.getOrder().compareTo(another.getOrder());
	}

	@Override
	public String toString() {
		return "EMSColumnAnalysis [propertyDescriptor=" + propertyDescriptor + ", order=" + order + ", name=" + name
				+ ", type=" + type + ", length=" + length + ", scale=" + scale + ", nullable=" + nullable + ", comment="
				+ comment + ", created=" + created + ", mainEntity=" + mainEntity + "]";
	}

}
