package com.gemo.dto.build;

import java.util.ArrayList;
import java.util.List;

public class EMSTable {
	
	private List<String> tableNameList = new ArrayList<String>();
	
	private String schemaName;
	
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public List<String> getTableNameList() {
		return tableNameList;
	}

	public void setTableNameList(List<String> tableNameList) {
		this.tableNameList = tableNameList;
	}
	
}
