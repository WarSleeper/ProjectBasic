package com.gemo.dto.build;

import java.util.ArrayList;
import java.util.List;

public class EMSUpdate {

	//
	private EMSTable table = new EMSTable();
	//排序语句
	private StringBuffer setSql = new StringBuffer();
	//参数列表
	private List<Object> paramList = new ArrayList<Object>();
	
	public EMSTable getTable() {
		return table;
	}
	public void setTable(EMSTable table) {
		this.table = table;
	}
	public StringBuffer getSetSql() {
		return setSql;
	}
	public void setSetSql(StringBuffer setSql) {
		this.setSql = setSql;
	}
	public List<Object> getParamList() {
		return paramList;
	}
	public void setParamList(List<Object> paramList) {
		this.paramList = paramList;
	}
	
}
