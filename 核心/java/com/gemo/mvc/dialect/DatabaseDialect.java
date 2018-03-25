package com.gemo.mvc.dialect;

import java.util.List;

import com.gemo.mvc.pojo.BusinessObject;

/**
 * 分页、分表工具
 */
public interface DatabaseDialect {

	/**
	 * 获取分页Sql
	 * 
	 * @param noPagingSql
	 *            未装配分页查询的sql
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            分页记录数
	 * @return
	 */
	public String getPagingSql(String noPagingSql, Long skip, Long limit);

	/**
	 * 获取建表SQL
	 * 
	 * @param <T>
	 * @param cls
	 * @return
	 */
	public <T extends BusinessObject> List<String> getCreateTableSql(Class<T> cls);

	/**
	 * 获取建库SQL
	 * 
	 * @return
	 */
	public List<String> getCreateSchemaSql(String schemaName);

	public String getSchemaExistSql(String schemaName);

	/**
	 * 获取不同数据库的schema与table信息
	 * 
	 * @return
	 */
	public String getTableInfoSql();

	/**
	 * 获得真正表模式或者前缀
	 * 
	 * @param <T>
	 * @param cls
	 * @return
	 */
	public String getRealSchema(Class<?> cls);

	/**
	 * 表不存在JDBC编码
	 * 
	 * @return
	 */
	public String getTableNotExistCode();

	/**
	 * 
	 * @param cls
	 * @param length
	 * @param scale
	 * @return
	 */
	public String getDatabaseColumnType(Class<?> cls, Integer length, Integer scale);

	public void init() throws Exception;

	public Boolean isTableNotExists(Exception exception);
}
