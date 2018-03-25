package com.gemo.mvc.dao.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.annotation.Column;
import com.gemo.annotation.Entity;
import com.gemo.annotation.Redundant;
import com.gemo.constant.SystemSetting;
import com.gemo.dto.analysis.EMSAnalysis;
import com.gemo.dto.analysis.EMSEmbeddedAnalysis;
import com.gemo.dto.analysis.EMSTableAnalysis;
import com.gemo.dto.build.EMSCriteria;
import com.gemo.dto.build.EMSInsert;
import com.gemo.dto.build.EMSTable;
import com.gemo.dto.build.EMSUpdate;
import com.gemo.enumeration.EMSOrder;
import com.gemo.enumeration.SpecialOperator;
import com.gemo.mvc.dao.CoreDao;
import com.gemo.mvc.dialect.DatabaseDialect;
import com.gemo.mvc.pojo.BusinessObject;
import com.gemo.utils.BusinessObjectUtils;

@Repository("jdbcTemplateCoreDao")
@SuppressWarnings("unchecked")
public class CoreDaoImpl implements CoreDao {

	private static Logger log = Logger.getLogger(CoreDaoImpl.class);

	@Resource(name = "jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	@Resource
	protected SystemSetting systemSetting;

	protected DatabaseDialect databaseDialect;
	@Resource
	protected BusinessObjectUtils businessObjectUtils;
	@Resource
	private EMSAnalysis analysis;

	@PostConstruct
	public void init() throws Exception {
		databaseDialect = (DatabaseDialect) systemSetting.getContext().getBean(systemSetting.getJdbcDriverClass());
	}

	@Override
	public int executeUpdate(String sql, List<Object> args) throws Exception {
		// TODO Auto-generated method stub
		if (args == null) {
			args = new ArrayList<Object>();
		}
		return jdbcTemplate.update(sql, args.toArray());
	}

	@Override
	public List<Map<String, Object>> queryBySql(String sql, List<Object> args) throws Exception {
		// TODO Auto-generated method stub
		if (args == null) {
			args = new ArrayList<Object>();
		}
		return jdbcTemplate.queryForList(sql, args.toArray());
	}

	@Override
	public <T extends BusinessObject> List<T> query(String sql, List<Object> args, final Class<T> cls)
			throws Exception {
		// TODO Auto-generated method stub
		List<T> list = jdbcTemplate.query(sql, args.toArray(), new RowMapper<T>() {

			@SuppressWarnings("rawtypes")
			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				T mapper = null;

				try {

					mapper = cls.newInstance();
					BeanInfo bi = Introspector.getBeanInfo(cls);
					PropertyDescriptor[] propertyList = bi.getPropertyDescriptors();

					for (PropertyDescriptor property : propertyList) {

						Field field = null;
						Class<?> tempClass = cls;
						while (tempClass != null) {
							try {
								field = tempClass.getDeclaredField(property.getName());
								break;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								tempClass = tempClass.getSuperclass();
							}
						}
						if (field == null) {
							continue;
						}

						Column column = field.getAnnotation(Column.class);
						if (column == null) {
							Method readMethod = property.getReadMethod();
							column = readMethod.getAnnotation(Column.class);
						}

						Class type = property.getPropertyType();
						if (column != null) {

							Method writeMethod = property.getWriteMethod();
							Object columnValue = null;

							if (rs.getObject(column.name()) != null) {
								if (type.equals(Integer.class)) {
									columnValue = rs.getInt(column.name());
								} else if (type.equals(Long.class)) {
									columnValue = rs.getLong(column.name());
								} else if (type.equals(Double.class)) {
									columnValue = rs.getDouble(column.name());
								} else if (type.equals(Date.class)) {
									java.sql.Timestamp sqlDate = rs.getTimestamp(column.name());
									columnValue = new Date(sqlDate.getTime());
								} else if (type.equals(Boolean.class)) {
									int key = rs.getInt(column.name());
									if (key == 1) {
										columnValue = true;
									} else {
										columnValue = false;
									}
								} else if (type.equals(String.class)) {
									columnValue = rs.getString(column.name());
								}
							}

							if (columnValue != null) {
								writeMethod.invoke(mapper, columnValue);
							}
						} else {
							try {
								Field f = cls.getDeclaredField(property.getName());
								Redundant ecf = f.getAnnotation(Redundant.class);
								if (ecf != null) {
									JsonProperty jp = f.getAnnotation(JsonProperty.class);
									if (jp != null) {
										Method writeMethod = property.getWriteMethod();
										Object columnValue = null;
										String redundantColumnName = jp.value();
										if (type.equals(Integer.class)) {
											columnValue = rs.getInt(redundantColumnName);
										} else if (type.equals(Long.class)) {
											columnValue = rs.getLong(redundantColumnName);
										} else if (type.equals(Double.class)) {
											columnValue = rs.getDouble(redundantColumnName);
										} else if (type.equals(Date.class)) {
											java.sql.Timestamp sqlDate = rs.getTimestamp(redundantColumnName);
											columnValue = new Date(sqlDate.getTime());
										} else if (type.equals(Boolean.class)) {
											columnValue = rs.getBoolean(redundantColumnName);
										} else if (type.equals(String.class)) {
											columnValue = rs.getString(redundantColumnName);
										}
										if (columnValue != null) {
											writeMethod.invoke(mapper, columnValue);
										}
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
							}
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return mapper;
			}

		});

		return list;
	}

	@Override
	public <T extends BusinessObject> List<T> query(T object) throws Exception {
		// TODO Auto-generated method stub
		return this.query(object, false);
	}

	@Override
	public <T extends BusinessObject> List<T> query(T object, Boolean isCascade) throws Exception {
		// TODO Auto-generated method stub
		EMSCriteria criteria = new EMSCriteria();
		businessObjectUtils.buildEMSCriteria(true, 0, null, object, criteria);

		List<T> finalList = new ArrayList<T>();

		Long limit = object.getLimit();
		Long skip = object.getSkip();

		if (limit == null || limit < 0) {
			limit = Long.MAX_VALUE;
		}
		if (skip == null || skip < 0) {
			skip = 0l;
		}

		Long total = skip + limit;

		EMSTable table = criteria.getTable();
		for (String tableNameTemp : table.getTableNameList()) {
			if (total == 0) {
				break;
			}
			StringBuffer sql = new StringBuffer(" select ");
			sql.append(criteria.getSelectSql());
			sql.append(" from ");
			if (!"".equals(table.getSchemaName())) {
				sql.append(table.getSchemaName() + "." + tableNameTemp + " ");
			} else {
				sql.append(tableNameTemp + " ");
			}
			sql.append(BusinessObjectUtils.prefix + "_0");
			sql.append(criteria.getWhereSql());
			sql.append(criteria.getOrderBySql());

			List<T> list = null;
			try {
				if (systemSetting.getSystemPrintSql()) {
					log.info("查询语句：" + sql);
					log.info("查询参数：" + criteria.getParamList());
				}
				if (table.getTableNameList().size() == 1) {
					// 单表查询
					String querySql = databaseDialect.getPagingSql(sql.toString(), skip, limit);
					finalList = (List<T>) this.query(querySql, criteria.getParamList(), object.getClass());
				} else {
					// 多表查询
					String querySql = databaseDialect.getPagingSql(sql.toString(), 0L, total);
					list = (List<T>) this.query(querySql, criteria.getParamList(), object.getClass());
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				checkTable(object, table.getSchemaName(), tableNameTemp, e);
			}

			// 多表-查到数据
			if (table.getTableNameList().size() > 1 && list.size() > 0) {
				// 跳过数据
				Long remainder = skip - list.size();
				if (remainder >= 0) {
					// 剩余需要跳过数据
					skip = remainder;
				} else if (remainder < 0) {
					// 无剩余需要跳过数据
					Long end = null;
					Long start = skip;
					// 分页与结果集比较
					Long remainderInner = total - list.size();
					// 下次不跳过数据
					skip = 0l;
					if (remainderInner >= 0) {
						// 分页大于结果集，集合内skip to list.size()符合
						end = new Long(list.size());
						// 设置下次页内数据
						limit = remainderInner;
					} else {
						// 分页小于结果集，集合内skip to limit符合
						end = total;
						// 设置下次页内数据
						limit = 0l;
					}

					for (long idx = start; idx < end; idx++) {
						finalList.add(list.get((int) idx));
					}

				}

				total = skip + limit;
			}
		}

		if (isCascade != null && isCascade) {
			// 主实体
			String entityName = (object.getClass().getAnnotation(Entity.class)).name();
			// 主表
			EMSTableAnalysis tableAnalysis = analysis.getEntityMap().get(entityName).getTable();
			// 内嵌集合
			Map<String, EMSEmbeddedAnalysis> embeddedMap = tableAnalysis.getEmbeddedMap();

			if (embeddedMap != null && embeddedMap.size() > 0) {
				// 主表主键字段
				PropertyDescriptor propertyDescriptorId = new PropertyDescriptor(tableAnalysis.getPrimaryKeyProperty(),
						object.getClass());
				for (T result : finalList) {
					// 主表主键值
					Object value = propertyDescriptorId.getReadMethod().invoke(result);
					for (Map.Entry<String, EMSEmbeddedAnalysis> embeddedEntry : embeddedMap.entrySet()) {
						// 主表内嵌属性
						String propertyName = embeddedEntry.getKey();
						// 内嵌分析
						EMSEmbeddedAnalysis embeddedAnalysis = embeddedEntry.getValue();
						// 内嵌实体属性名
						String embeddedProperty = embeddedAnalysis.getForeignKeyProperty();
						// 内嵌实体字节码
						Class<T> embeddedClass = (Class<T>) analysis.getEntityMap()
								.get(embeddedAnalysis.getEmbeddedEntity()).getCls();
						// 内嵌查询对象
						T embeddedObject = BusinessObjectUtils.getObject(embeddedClass,
								object.getBuildingForContainer(), object.getSplitTimeType(),
								object.getSplitEnergyType());
						// 内嵌实体属性
						PropertyDescriptor propertyDescriptor = new PropertyDescriptor(embeddedProperty, embeddedClass);
						// 将主实体主键值写入内嵌实体外键
						Method writeMethod = propertyDescriptor.getWriteMethod();
						writeMethod.invoke(embeddedObject, value);
						if (embeddedAnalysis.getOrderMap() != null) {
							for (Map.Entry<String, EMSOrder> entry : embeddedAnalysis.getOrderMap().entrySet()) {
								embeddedObject.setSort(entry.getKey(), entry.getValue());
							}
						}
						// 查询
						List<T> embeddedList = query(embeddedObject, true);
						// 将查询结果写入主实体内嵌属性
						PropertyDescriptor propertyDescriptorEmbeddedList = new PropertyDescriptor(propertyName,
								object.getClass());
						propertyDescriptorEmbeddedList.getWriteMethod().invoke(result, embeddedList);
					}
				}
			}
		}

		return finalList;
	}

	@Override
	public <T extends BusinessObject> int save(T object) throws Exception {
		// TODO Auto-generated method stub
		EMSInsert insert = new EMSInsert();
		businessObjectUtils.buildEMSInsert(object, insert);

		StringBuffer sql = new StringBuffer();
		sql.append("insert into ");
		if (!"".equals(insert.getSchema())) {
			sql.append(insert.getSchema() + "." + insert.getTable());
		} else {
			sql.append(insert.getTable());
		}
		sql.append(insert.getInsertIntoSql());
		sql.append(" values ");
		sql.append(insert.getValuesSql());

		int count = 0;
		try {
			if (systemSetting.getSystemPrintSql()) {
				log.info("查询语句：" + sql);
				log.info("查询参数：" + insert.getParamList());
			}
			count = this.executeUpdate(sql.toString(), insert.getParamList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			checkTable(object, insert.getSchema(), insert.getTable(), e);
			count = this.executeUpdate(sql.toString(), insert.getParamList());
		}

		return count;
	}

	@Override
	public <T extends BusinessObject> int remove(T object) throws Exception {
		// TODO Auto-generated method stub
		return this.remove(object, false);
	}

	@Override
	public <T extends BusinessObject> int update(T objectCriteria, T objectUpdate) throws Exception {
		// TODO Auto-generated method stub

		if (!(objectCriteria.getClass() == objectUpdate.getClass())) {
			throw new RuntimeException(" 查询对象与更新对象不一致！");
		}

		EMSCriteria criteria = new EMSCriteria();
		businessObjectUtils.buildEMSCriteria(false, 0, null, objectCriteria, criteria);

		EMSUpdate update = new EMSUpdate();
		businessObjectUtils.buildEMSUpdate(objectUpdate, update);

		EMSTable table = criteria.getTable();

		int count = 0;
		for (String tableNameTemp : table.getTableNameList()) {

			String sql;
			if (!"".equals(table.getSchemaName())) {
				sql = " update " + table.getSchemaName() + "." + tableNameTemp + " " + update.getSetSql()
						+ criteria.getWhereSql();
			} else {
				sql = " update " + tableNameTemp + " " + update.getSetSql() + criteria.getWhereSql();
			}

			List<Object> args = new ArrayList<Object>();
			args.addAll(update.getParamList());
			args.addAll(criteria.getParamList());
			try {
				if (systemSetting.getSystemPrintSql()) {
					log.info("查询语句：" + sql);
					log.info("查询参数：" + args);
				}

				count = count + this.executeUpdate(sql, args);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				checkTable(objectUpdate, table.getSchemaName(), tableNameTemp, e);
			}

		}

		return count;
	}

	@Override
	public int count(String sql, List<Object> args) throws Exception {
		// TODO Auto-generated method stub
		Integer count = jdbcTemplate.queryForObject(sql, args.toArray(), Integer.class);
		return count;
	}

	@Override
	public <T extends BusinessObject> int count(T object) throws Exception {
		// TODO Auto-generated method stub
		EMSCriteria criteria = new EMSCriteria();
		businessObjectUtils.buildEMSCriteria(false, 0, null, object, criteria);

		EMSTable table = criteria.getTable();

		int count = 0;
		for (String tableNameTemp : table.getTableNameList()) {
			String sql;
			if (!"".equals(table.getSchemaName())) {
				sql = " select count(*) from " + table.getSchemaName() + "." + tableNameTemp + " "
						+ criteria.getWhereSql();
			} else {
				sql = " select count(*) from " + tableNameTemp + " " + criteria.getWhereSql();
			}
			try {
				if (systemSetting.getSystemPrintSql()) {
					log.info("查询语句：" + sql);
					log.info("查询参数：" + criteria.getParamList());
				}
				count = count + this.count(sql, criteria.getParamList());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				checkTable(object, table.getSchemaName(), tableNameTemp, e);
			}
		}

		return count;
	}

	@Override
	public <T extends BusinessObject> int remove(T object, Boolean isCascade) throws Exception {
		// TODO Auto-generated method stub
		int count = 0;

		// 主实体
		String entityName = (object.getClass().getAnnotation(Entity.class)).name();
		// 主表
		EMSTableAnalysis tableAnalysis = analysis.getEntityMap().get(entityName).getTable();
		// 内嵌集合
		Map<String, EMSEmbeddedAnalysis> embeddedMap = tableAnalysis.getEmbeddedMap();

		if (isCascade != null && isCascade && embeddedMap != null && embeddedMap.size() > 0) {

			// 主表主键字段
			PropertyDescriptor propertyDescriptorId = new PropertyDescriptor(tableAnalysis.getPrimaryKeyProperty(),
					object.getClass());
			List<T> removelist = this.query(object);
			List<Object> valueList = new ArrayList<Object>();
			for (T removeObject : removelist) {
				valueList.add(propertyDescriptorId.getReadMethod().invoke(removeObject));
			}
			if (valueList.size() > 0) {
				for (Map.Entry<String, EMSEmbeddedAnalysis> embeddedEntry : embeddedMap.entrySet()) {
					// 内嵌分析
					EMSEmbeddedAnalysis embeddedAnalysis = embeddedEntry.getValue();
					// 内嵌实体属性名
					String embeddedProperty = embeddedAnalysis.getForeignKeyProperty();
					// 内嵌实体字节码
					Class<T> embeddedClass = (Class<T>) analysis.getEntityMap()
							.get(embeddedAnalysis.getEmbeddedEntity()).getCls();
					// 内嵌查询对象
					T embeddedObject = BusinessObjectUtils.getObject(embeddedClass, object.getBuildingForContainer(),
							object.getSplitTimeType(), object.getSplitEnergyType());
					embeddedObject.setSpecialOperation(embeddedProperty, SpecialOperator.$in, valueList);
					// 级联删除
					this.remove(embeddedObject, true);
				}
				object = (T) BusinessObjectUtils.getObject(object.getClass(), object.getBuildingForContainer(),
						object.getSplitTimeType(), object.getSplitEnergyType());
				object.setSpecialOperation(tableAnalysis.getPrimaryKeyProperty(), SpecialOperator.$in, valueList);
			}

		}

		EMSCriteria criteria = new EMSCriteria();
		businessObjectUtils.buildEMSCriteria(false, 0, null, object, criteria);

		EMSTable table = criteria.getTable();
		for (String tableNameTemp : table.getTableNameList()) {

			StringBuffer sql = new StringBuffer(" delete from ");
			if (!"".equals(table.getSchemaName())) {
				sql.append(table.getSchemaName() + "." + tableNameTemp);
			} else {
				sql.append(tableNameTemp);
			}
			sql.append(criteria.getWhereSql());

			try {
				if (systemSetting.getSystemPrintSql()) {
					log.info("查询语句：" + sql);
					log.info("查询参数：" + criteria.getParamList());
				}
				count = count + this.executeUpdate(sql.toString(), criteria.getParamList());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				checkTable(object, table.getSchemaName(), tableNameTemp, e);
			}
		}

		return count;
	}

	private <T extends BusinessObject> void checkTable(T object, String schemaName, String tableName, Exception e)
			throws Exception {
		if (databaseDialect.isTableNotExists(e)) {
			if (!"".equals(schemaName)) {
				log.info("建表：" + schemaName + "." + tableName);
			} else {
				log.info("建表：" + tableName);
			}
			List<String> createTableSqlList = databaseDialect.getCreateTableSql(object.getClass());
			for (String createTableSql : createTableSqlList) {
				String realSql = BusinessObjectUtils.createTableSqlReplace(createTableSql, schemaName, tableName);
				this.executeUpdate(realSql, null);
			}
		} else {
			throw e;
		}
	}

	@Override
	public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws Exception {
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	@Override
	public <T extends BusinessObject> List<T> batchQuery(List<T> objectList, LinkedHashMap<String, EMSOrder> sortMap,
			Long skip, Long limit) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception("对不起！暂不支持！");
	}

	@Override
	public <T extends BusinessObject> int save(List<T> objectList) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception("对不起！暂不支持！");
	}

	@Override
	public <T extends BusinessObject> int remove(List<T> objectList) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception("对不起！暂不支持！");
	}

}
