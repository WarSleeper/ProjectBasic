package com.gemo.dto.build;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建查询
 */
public class EMSCriteria {

	//
	private EMSTable table = new EMSTable();
	// 字段语句
	private StringBuffer selectSql = new StringBuffer();
	// 查询语句
	private StringBuffer whereSql = new StringBuffer();
	// 排序语句
	private StringBuffer orderBySql = new StringBuffer();
	// 参数列表
	private List<Object> paramList = new ArrayList<Object>();

	public StringBuffer getSelectSql() {
		return selectSql;
	}

	public void setSelectSql(StringBuffer selectSql) {
		this.selectSql = selectSql;
	}

	public StringBuffer getWhereSql() {
		return whereSql;
	}

	public void setWhereSql(StringBuffer whereSql) {
		this.whereSql = whereSql;
	}

	public List<Object> getParamList() {
		return paramList;
	}

	public void setParamList(List<Object> paramList) {
		this.paramList = paramList;
	}

	public StringBuffer getOrderBySql() {
		return orderBySql;
	}

	public void setOrderBySql(StringBuffer orderBySql) {
		this.orderBySql = orderBySql;
	}

	public EMSTable getTable() {
		return table;
	}

	public void setTable(EMSTable table) {
		this.table = table;
	}

}
