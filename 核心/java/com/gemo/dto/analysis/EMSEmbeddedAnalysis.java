package com.gemo.dto.analysis;

import java.util.Map;

import com.gemo.enumeration.EMSOrder;

/**
 * EMS ORM级联解析
 */
public class EMSEmbeddedAnalysis {

	// 是否属性级联
	private Boolean isProperty;
	// 级联实体
	private String embeddedEntity;
	// 级联实体外键
	private String foreignKey;
	// 级联实体外键属性
	private String foreignKeyProperty;

	private Map<String, EMSOrder> orderMap;

	public Boolean getIsProperty() {
		return isProperty;
	}

	public void setIsProperty(Boolean isProperty) {
		this.isProperty = isProperty;
	}

	public String getEmbeddedEntity() {
		return embeddedEntity;
	}

	public void setEmbeddedEntity(String embeddedEntity) {
		this.embeddedEntity = embeddedEntity;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String getForeignKeyProperty() {
		return foreignKeyProperty;
	}

	public void setForeignKeyProperty(String foreignKeyProperty) {
		this.foreignKeyProperty = foreignKeyProperty;
	}

	public Map<String, EMSOrder> getOrderMap() {
		return orderMap;
	}

	public void setOrderMap(Map<String, EMSOrder> orderMap) {
		this.orderMap = orderMap;
	}

	@Override
	public String toString() {
		return "EMSEmbeddedAnalysis [isProperty=" + isProperty + ", embeddedEntity=" + embeddedEntity + ", foreignKey="
				+ foreignKey + ", foreignKeyProperty=" + foreignKeyProperty + ", orderMap=" + orderMap + "]";
	}

}
