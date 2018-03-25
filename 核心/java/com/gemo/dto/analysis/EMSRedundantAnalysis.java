package com.gemo.dto.analysis;

/**
 * EMS ORM冗余解析
 */
public class EMSRedundantAnalysis {

	// 关联实体
	private String mainEntity;
	// 关联实体值字段
	private String valueColumn;
	// 当前实体关联关联实体外键字段
	private String foreignColumn;
	// 冗余显示字段名称
	private String redundantName;

	public String getMainEntity() {
		return mainEntity;
	}

	public void setMainEntity(String mainEntity) {
		this.mainEntity = mainEntity;
	}

	public String getValueColumn() {
		return valueColumn;
	}

	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}

	public String getForeignColumn() {
		return foreignColumn;
	}

	public void setForeignColumn(String foreignColumn) {
		this.foreignColumn = foreignColumn;
	}

	public String getRedundantName() {
		return redundantName;
	}

	public void setRedundantName(String redundantName) {
		this.redundantName = redundantName;
	}

	@Override
	public String toString() {
		return "EMSRedundantAnalysis [mainEntity=" + mainEntity + ", valueColumn=" + valueColumn + ", foreignColumn="
				+ foreignColumn + ", redundantName=" + redundantName + "]";
	}

}
