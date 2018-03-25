package com.gemo.dto.analysis;

import java.util.List;
import java.util.Map;

/**
 * EMS ORM解析 Table
 */
public class EMSTableAnalysis {

	// 表所在数据库或者用户
	private String schema;
	// 表名称
	private String name;
	// 描述
	private String comment;
	// 索引
	private List<EMSIndexAnalysis> indexList;
	// 数据库字段
	private Map<String, EMSColumnAnalysis> columnMap;
	// 冗余字段
	private Map<String, EMSRedundantAnalysis> redundantMap;
	// 级联字段
	private Map<String, EMSEmbeddedAnalysis> embeddedMap;
	// 主键
	private String primaryKey;
	// 主键属性
	private String primaryKeyProperty;
	// 按月分表字段
	private String month;
	// 是否事务
	private Boolean transaction;
	// 是否已创建
	private Boolean created;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Map<String, EMSColumnAnalysis> getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(Map<String, EMSColumnAnalysis> columnMap) {
		this.columnMap = columnMap;
	}

	public List<EMSIndexAnalysis> getIndexList() {
		return indexList;
	}

	public void setIndexList(List<EMSIndexAnalysis> indexList) {
		this.indexList = indexList;
	}

	public Boolean getCreated() {
		return created;
	}

	public void setCreated(Boolean created) {
		this.created = created;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Boolean getTransaction() {
		return transaction;
	}

	public void setTransaction(Boolean transaction) {
		this.transaction = transaction;
	}

	public Map<String, EMSRedundantAnalysis> getRedundantMap() {
		return redundantMap;
	}

	public void setRedundantMap(Map<String, EMSRedundantAnalysis> redundantMap) {
		this.redundantMap = redundantMap;
	}

	public Map<String, EMSEmbeddedAnalysis> getEmbeddedMap() {
		return embeddedMap;
	}

	public void setEmbeddedMap(Map<String, EMSEmbeddedAnalysis> embeddedMap) {
		this.embeddedMap = embeddedMap;
	}

	public String getPrimaryKeyProperty() {
		return primaryKeyProperty;
	}

	public void setPrimaryKeyProperty(String primaryKeyProperty) {
		this.primaryKeyProperty = primaryKeyProperty;
	}

	@Override
	public String toString() {
		return "EMSTableAnalysis [schema=" + schema + ", name=" + name + ", comment=" + comment + ", indexList="
				+ indexList + ", columnMap=" + columnMap + ", redundantMap=" + redundantMap + ", embeddedMap="
				+ embeddedMap + ", primaryKey=" + primaryKey + ", primaryKeyProperty=" + primaryKeyProperty + ", month="
				+ month + ", transaction=" + transaction + ", created=" + created + "]";
	}

}
