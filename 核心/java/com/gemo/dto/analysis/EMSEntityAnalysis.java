package com.gemo.dto.analysis;

/**
 * EMS ORM解析Entity
 */
public class EMSEntityAnalysis {

	private String name;

	private Class<?> cls;

	private EMSTableAnalysis table;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EMSTableAnalysis getTable() {
		return table;
	}

	public void setTable(EMSTableAnalysis table) {
		this.table = table;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	@Override
	public String toString() {
		return "EMSEntityAnalysis [name=" + name + ", cls=" + cls + ", table=" + table + "]";
	}

}
