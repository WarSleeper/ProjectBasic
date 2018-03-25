package com.gemo.mvc.dialect.impl.sqlserver;

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
 * 底层为SqlServer
 */
@Component(JdbcDriverClass.SQLSERVER)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SqlServerDialect extends BaseDatabaseDialect implements DatabaseDialect {

	private static Logger log = Logger.getLogger(SqlServerDialect.class);

	@Override
	public String getPagingSql(String noPagingSql, Long skip, Long limit) {
		// TODO Auto-generated method stub
		if (skip == null || skip < 0) {
			skip = 0l;
		}
		StringBuffer sql = new StringBuffer();
		int selectIdx = noPagingSql.indexOf("select");
		String selectStr = noPagingSql.substring(0, selectIdx + 6);
		String otherStr = noPagingSql.substring(selectIdx + 6);
		if (skip == 0) {
			sql.append(selectStr);
			sql.append(" top " + limit + " ");
			sql.append(otherStr);
		} else {
			int orderByIdx = otherStr.indexOf("order by");
			if (orderByIdx == -1) {
				throw new RuntimeException(" The sql is marked paging, so it must have order by clause. ");
			}
			String middleStr = otherStr.substring(0, orderByIdx);
			String orderByStr = otherStr.substring(orderByIdx);
			sql.append(" select * from (");
			sql.append(selectStr);
			sql.append(" ROW_NUMBER() OVER (" + orderByStr + ") as ems_rownum , ");
			sql.append(middleStr);
			sql.append(") ems where ems.ems_rownum between " + (skip + 1) + " and " + (skip + limit));
		}
		return sql.toString();
	}

	@Override
	public List<String> getCreateSchemaSql(String schemaName) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		list.add("create database " + schemaName);
		return list;
	}

	@Override
	public String getTableInfoSql() {
		// TODO Auto-generated method stub
		return "select name table_schema from master.dbo.sysdatabases";
	}

	@Override
	public String getRealSchema(Class<?> cls) {
		// TODO Auto-generated method stub
		return super.getRealSchema(cls) + "." + "dbo";
	}

	@Override
	public String getTableNotExistCode() {
		// TODO Auto-generated method stub
		return "208";
	}

	@Override
	public String getSchemaExistSql(String schemaName) {
		// TODO Auto-generated method stub
		return "select * from sys.databases where name = '" + schemaName + "'";
	}

	@Override
	public String getDatabaseColumnType(Class<?> cls, Integer length, Integer scale) {
		// TODO Auto-generated method stub
		String columnType = null;
		if (cls == String.class) {
			columnType = "nvarchar";
		} else if (cls == Long.class || cls == Integer.class || cls == Boolean.class) {
			columnType = "int";
		} else if (cls == Double.class) {
			columnType = "decimal";
		} else if (cls == Date.class) {
			columnType = "datetime2";
		} else {
			throw new RuntimeException("对象字段类型不支持" + cls);
		}

		return columnType;
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		log.info("******** SqlServer配置初始化开始 ********");
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
				if (column.getType().equals("datetime2")) {
					column.setLength(7);
					column.setScale(0);
				}
				if (column.getType().equals("int")) {
					column.setLength(0);
				}
				if (column.getType().equals("nvarchar")) {
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
		log.info("******** SqlServer配置初始化结束 ********");
		// System.out.println(SystemConstant.jsonMapper.writeValueAsString(tableMap));
	}

}
