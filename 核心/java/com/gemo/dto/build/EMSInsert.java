package com.gemo.dto.build;

import java.util.ArrayList;
import java.util.List;

public class EMSInsert {

	private String schema;
	
	private String table;
	
	private StringBuffer insertIntoSql = new StringBuffer();
	
	private StringBuffer valuesSql = new StringBuffer();
	
	private List<Object> paramList = new ArrayList<Object>();

	public List<Object> getParamList() {
		return paramList;
	}

	public void setParamList(List<Object> paramList) {
		this.paramList = paramList;
	}

	public StringBuffer getInsertIntoSql() {
		return insertIntoSql;
	}

	public void setInsertIntoSql(StringBuffer insertIntoSql) {
		this.insertIntoSql = insertIntoSql;
	}

	public StringBuffer getValuesSql() {
		return valuesSql;
	}

	public void setValuesSql(StringBuffer valuesSql) {
		this.valuesSql = valuesSql;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
}
