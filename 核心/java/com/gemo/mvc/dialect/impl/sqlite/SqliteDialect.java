package com.gemo.mvc.dialect.impl.sqlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.gemo.constant.SystemConstant.JdbcDriverClass;
import com.gemo.dto.analysis.EMSColumnAnalysis;
import com.gemo.dto.analysis.EMSEntityAnalysis;
import com.gemo.dto.analysis.EMSIndexAnalysis;
import com.gemo.dto.analysis.EMSTableAnalysis;
import com.gemo.mvc.dialect.BaseDatabaseDialect;
import com.gemo.mvc.dialect.DatabaseDialect;
import com.gemo.utils.CommonUtils;

@Component(JdbcDriverClass.SQLITE)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SqliteDialect extends BaseDatabaseDialect implements DatabaseDialect {

	private static Logger log = Logger.getLogger(SqliteDialect.class);

	@Override
	public String getPagingSql(String noPagingSql, Long skip, Long limit) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer();
		sql.append(noPagingSql);
		if (skip == null || skip < 0) {
			skip = 0l;
		}
		if (limit == null || limit < 0) {
			limit = Long.MAX_VALUE;
		}
		sql.append(" limit " + limit + " offset " + skip);
		return sql.toString();
	}

	@Override
	public List<String> getCreateSchemaSql(String schemaName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchemaExistSql(String schemaName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableInfoSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableNotExistCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseColumnType(Class<?> cls, Integer length, Integer scale) {
		// TODO Auto-generated method stub
		String columnType = null;
		if (cls == String.class) {
			columnType = "varchar2";
		} else if (cls == Long.class || cls == Integer.class || cls == Boolean.class) {
			columnType = "int";
		} else if (cls == Double.class) {
			columnType = "decimal";
		} else if (cls == Date.class) {
			columnType = "datetime";
		} else {
			throw new RuntimeException("对象字段类型不支持" + cls);
		}

		return columnType;
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		log.info("******** Sqlite配置初始化开始 ********");
		Map root = new HashMap();
		for (Map.Entry<String, EMSEntityAnalysis> entityEntry : analysis.getEntityMap().entrySet()) {
			// 实体
			String entityKey = entityEntry.getKey();
			EMSEntityAnalysis entity = entityEntry.getValue();
			// 表
			EMSTableAnalysis table = entity.getTable();
			// 字段
			List<EMSColumnAnalysis> columnList = new ArrayList<EMSColumnAnalysis>();
			for (Map.Entry<String, EMSColumnAnalysis> columnEntry : table.getColumnMap().entrySet()) {
				EMSColumnAnalysis column = new EMSColumnAnalysis();
				BeanUtils.copyProperties(columnEntry.getValue(), column);
				if (column.getType().equals("datetime")) {
					column.setLength(0);
				}
				if (column.getType().equals("varchar2")) {
					if (column.getLength() > 4000) {
						column.setLength(4000);
					}
				}
				columnList.add(column);
			}
			Collections.sort(columnList);
			root.put("columnList", columnList);
			root.put("primaryKey", table.getPrimaryKey());
			List<String> ddlList = tableMap.get(entityKey);
			if (ddlList == null) {
				ddlList = new ArrayList<String>();
				tableMap.put(entityKey, ddlList);
			}
			String tableCreateSql = CommonUtils.getSqlByTemplate(root, "TableCreate.ftl");

			ddlList.add(tableCreateSql);
			root.clear();
			for (EMSIndexAnalysis index : table.getIndexList()) {
				root.put("index", index);
				ddlList.add(CommonUtils.getSqlByTemplate(root, "IndexCreate.ftl"));
			}
		}
		log.info("******** Sqlite配置初始化结束 ********");
	}

	@Override
	public Boolean isTableNotExists(Exception exception) {
		// TODO Auto-generated method stub
		Throwable throwable = exception.getCause();
		if (!(throwable instanceof SQLException)) {
			return false;
		}
		SQLException se = (SQLException) throwable;
		String message = se.getMessage();
		if (message != null && message.indexOf("no such table") != -1) {
			return true;
		}
		return false;
	}

}
