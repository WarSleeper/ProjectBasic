package com.gemo.mvc.dialect.impl.mysql;

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

/**
 * 底层为MySql
 */
@Component(JdbcDriverClass.MYSQL)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MySqlDialect extends BaseDatabaseDialect implements DatabaseDialect {

	private static Logger log = Logger.getLogger(MySqlDialect.class);

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
		sql.append(" limit " + skip + "," + limit);
		return sql.toString();
	}

	@Override
	public List<String> getCreateSchemaSql(String schemaName) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		list.add("CREATE DATABASE " + schemaName + " DEFAULT CHARSET utf8 COLLATE utf8_general_ci");
		return list;
	}

	@Override
	public String getTableInfoSql() {
		// TODO Auto-generated method stub
		return "select table_schema,table_name from information_schema.tables";
	}

	@Override
	public String getTableNotExistCode() {
		// TODO Auto-generated method stub
		return "1146";
	}

	@Override
	public String getSchemaExistSql(String schemaName) {
		// TODO Auto-generated method stub
		return "select * from information_schema.SCHEMATA where SCHEMA_NAME = '" + schemaName + "'";
	}

	@Override
	public String getDatabaseColumnType(Class<?> cls, Integer length, Integer scale) {
		// TODO Auto-generated method stub
		String columnType = null;
		if (cls == String.class) {
			if (length > 4000) {
				columnType = "mediumtext";
			} else {
				columnType = "varchar";
			}
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
		log.info("******** MySql配置初始化开始 ********");
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
				if (column.getType().equals("mediumtext") || column.getType().equals("datetime")) {
					column.setLength(0);
				}
				columnList.add(column);
			}
			Collections.sort(columnList);
			root.put("columnList", columnList);
			root.put("primaryKey", table.getPrimaryKey());
			root.put("database", "mysql");
			List<String> ddlList = tableMap.get(entityKey);
			if (ddlList == null) {
				ddlList = new ArrayList<String>();
				tableMap.put(entityKey, ddlList);
			}
			String tableCreateSql = CommonUtils.getSqlByTemplate(root, "TableCreate.ftl");
			String engine = null;
			if (table.getTransaction()) {
				engine = "InnoDB";
			} else {
				engine = "MyISAM";
			}
			tableCreateSql = tableCreateSql + " ENGINE=" + engine + " DEFAULT CHARSET=utf8";

			ddlList.add(tableCreateSql);
			root.clear();
			for (EMSIndexAnalysis index : table.getIndexList()) {
				root.put("index", index);
				ddlList.add(CommonUtils.getSqlByTemplate(root, "IndexCreate.ftl"));
			}
		}
		log.info("******** MySql配置初始化结束 ********");
		// System.out.println(SystemConstant.jsonMapper.writeValueAsString(tableMap));
	}

}
