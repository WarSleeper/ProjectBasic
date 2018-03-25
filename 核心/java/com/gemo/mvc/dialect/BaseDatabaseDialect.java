package com.gemo.mvc.dialect;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.gemo.annotation.Entity;
import com.gemo.annotation.Table;
import com.gemo.constant.SchemaConstant;
import com.gemo.constant.SchemaConstant.Schema;
import com.gemo.dto.analysis.EMSAnalysis;
import com.gemo.mvc.pojo.BusinessObject;

public abstract class BaseDatabaseDialect implements DatabaseDialect {

	// 建表语句
	protected Map<String, List<String>> tableMap = new HashMap<String, List<String>>();
	// ORM解析类
	@Resource
	protected EMSAnalysis analysis;

	@Override
	public <T extends BusinessObject> List<String> getCreateTableSql(Class<T> cls) {
		// TODO Auto-generated method stub
		Entity entity = cls.getAnnotation(Entity.class);
		String entityName = entity.name();
		return tableMap.get(entityName);
	}

	@Override
	public String getRealSchema(Class<?> cls) {
		// TODO Auto-generated method stub
		Table table = cls.getAnnotation(Table.class);
		String schema;
		if (!(table.schema() == Schema.NONE)) {
			schema = SchemaConstant.getSchema(table.schema().getValue());
		} else {
			schema = "";
		}

		return schema;
	}

	@Override
	public Boolean isTableNotExists(Exception exception) {
		// TODO Auto-generated method stub
		Throwable throwable = exception.getCause();
		if (!(throwable instanceof SQLException)) {
			return false;
		}
		SQLException se = (SQLException) throwable;
		String errorCode = new Integer(se.getErrorCode()).toString();
		if (this.getTableNotExistCode().equalsIgnoreCase(errorCode)) {
			return true;
		}
		return false;
	}

}
