package com.gemo.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.annotation.Dimension;
import com.gemo.annotation.Entity;
import com.gemo.annotation.Table;
import com.gemo.constant.EMSConstant.Operator;
import com.gemo.constant.SystemConstant;
import com.gemo.constant.SystemConstant.JdbcDriverClass;
import com.gemo.constant.SystemSetting;
import com.gemo.dto.analysis.EMSAnalysis;
import com.gemo.dto.analysis.EMSColumnAnalysis;
import com.gemo.dto.analysis.EMSEmbeddedAnalysis;
import com.gemo.dto.analysis.EMSEntityAnalysis;
import com.gemo.dto.analysis.EMSRedundantAnalysis;
import com.gemo.dto.analysis.EMSTableAnalysis;
import com.gemo.dto.build.EMSCriteria;
import com.gemo.dto.build.EMSInsert;
import com.gemo.dto.build.EMSMonthRange;
import com.gemo.dto.build.EMSUpdate;
import com.gemo.enumeration.EMSDimension;
import com.gemo.enumeration.EMSOrder;
import com.gemo.enumeration.SpecialOperator;
import com.gemo.mvc.pojo.BusinessObject;
import com.gemo.mvc.pojo.SpecialOperation;
import com.gemo.thread.EmsMonthThread;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BusinessObjectUtils {

	public static final String prefix = "t";

	public static final String redundantPrefix = "r";

	public static final String subPrefix = "s";

	private static final Random random = new Random();

	@Resource
	private SystemSetting systemSetting;
	@Resource
	private EMSAnalysis analysis;

	public <T extends BusinessObject> void buildEMSCriteria(Boolean isSelect, Integer level, Integer embeddedIdx,
			T object, EMSCriteria criteria) throws Exception {

		if (object == null) {
			throw new Exception("查询对象模板不能为空！");
		}

		if (!object.check()) {
			throw new Exception("查询对象模板不能为附加结构错误！");
		}

		if (level == null || level < 0) {
			level = 0;
		}

		if (isSelect == null) {
			isSelect = true;
		}

		Boolean isPrefix;
		if (level == 0 && !isSelect) {
			isPrefix = false;
		} else {
			isPrefix = true;
		}

		String prefixTemp;
		if (isPrefix) {
			if (level == 0) {
				prefixTemp = prefix + "_" + level + ".";
			} else {
				prefixTemp = subPrefix + "_" + level + "_" + embeddedIdx + ".";
			}

		} else {
			prefixTemp = "";
		}

		StringBuffer selectSql = criteria.getSelectSql();

		StringBuffer whereSql = criteria.getWhereSql();
		whereSql.append(" where 1=1 ");

		StringBuffer orderBySql = criteria.getOrderBySql();
		List<Object> paramList = criteria.getParamList();

		StringBuffer subquerySql = new StringBuffer();
		List<Object> subqueryParamList = new ArrayList<Object>();

		Class cls = object.getClass();
		Entity entity = (Entity) cls.getAnnotation(Entity.class);

		EMSEntityAnalysis entityAnalysis = analysis.getEntityMap().get(entity.name());
		EMSTableAnalysis tableAnalysis = entityAnalysis.getTable();
		Map<String, EMSColumnAnalysis> columnMap = tableAnalysis.getColumnMap();
		Map<String, EMSRedundantAnalysis> redundantMap = tableAnalysis.getRedundantMap();
		Map<String, EMSEmbeddedAnalysis> embeddedMap = tableAnalysis.getEmbeddedMap();

		boolean isDesc = false;
		boolean isSplit = false;
		// 按月分表精确查询参数
		EMSMonthRange monthRange = new EMSMonthRange();
		if (level == 0) {
			criteria.getTable().setSchemaName(tableAnalysis.getSchema());
			// 按月分表
			if (tableAnalysis.getMonth() != null && !"".equals(tableAnalysis.getMonth())
					&& systemSetting.getJdbcDriverClass().equals(JdbcDriverClass.MYSQL)) {
				isSplit = true;
				monthRange.setEqualList(new ArrayList<Date>());
			}
			// 排序
			boolean isFirst = true;
			for (Map.Entry<String, EMSOrder> entry : object.getSortMap().entrySet()) {
				String propertyName = entry.getKey();
				EMSColumnAnalysis columnAnalysis = columnMap.get(propertyName);
				if (columnAnalysis != null) {
					EMSOrder ascDesc = entry.getValue();
					if (ascDesc == EMSOrder.Desc && isSplit) {
						isDesc = true;
					}
					if (isFirst) {
						orderBySql.append(" order by ");
					} else {
						orderBySql.append(",");
					}
					orderBySql.append(prefixTemp + columnAnalysis.getName() + " " + ascDesc.name());
					isFirst = false;
				}
			}

			int redundantIdx = 0;
			if (isSelect) {
				selectSql.append(" " + prefixTemp + "* ");
			}
			for (Map.Entry<String, EMSRedundantAnalysis> redundantEntry : redundantMap.entrySet()) {
				String propertyName = redundantEntry.getKey();
				EMSRedundantAnalysis redundantAnalysis = redundantEntry.getValue();

				String mainEntity = redundantAnalysis.getMainEntity();
				EMSEntityAnalysis redundantEntityAnalysis = analysis.getEntityMap().get(mainEntity);
				String redundantTableName = getTableName(getObject((Class<T>) (redundantEntityAnalysis.getCls()),
						object.getBuildingForContainer(), object.getSplitTimeType(), object.getSplitEnergyType()));
				String valueColumn = redundantAnalysis.getValueColumn();
				String foreignColumn = redundantAnalysis.getForeignColumn();
				String redundantName = redundantAnalysis.getRedundantName();
				String redundantPrefixTemp = redundantPrefix + redundantIdx;

				if (isSelect) {
					// 拼接冗余字段
					selectSql.append(",(select ");
					selectSql.append(redundantPrefixTemp + "." + valueColumn);
					selectSql.append(" from ");
					if (!"".equals(redundantEntityAnalysis.getTable().getSchema())) {
						selectSql.append(redundantEntityAnalysis.getTable().getSchema() + "." + redundantTableName + " "
								+ redundantPrefixTemp);
					} else {
						selectSql.append(redundantTableName + " " + redundantPrefixTemp);
					}
					selectSql.append(" where ");
					selectSql.append(redundantPrefixTemp + "." + redundantEntityAnalysis.getTable().getPrimaryKey());
					selectSql.append(" = ");
					selectSql.append(prefixTemp + foreignColumn);
					selectSql.append(") ");
					selectSql.append(redundantName);
				}

				StringBuffer subquerySqlTemp = new StringBuffer();
				buildWhereSql(subquerySqlTemp, subqueryParamList, isSplit, object, propertyName, "", null);

				if (subquerySqlTemp.length() > 0) {
					// 子查询
					String subPrefixTemp = redundantPrefix + "_" + (level + 1) + "_" + redundantIdx;
					subquerySql.append(" and " + prefixTemp + foreignColumn + " in ( ");
					subquerySql.append(" select ");
					subquerySql.append(subPrefixTemp + "." + redundantEntityAnalysis.getTable().getPrimaryKey());
					subquerySql.append(" from ");
					if (!"".equals(redundantEntityAnalysis.getTable().getSchema())) {
						subquerySql.append(redundantEntityAnalysis.getTable().getSchema() + "." + redundantTableName
								+ " " + subPrefixTemp);
					} else {
						subquerySql.append(redundantTableName + " " + subPrefixTemp);
					}
					subquerySql.append(" where " + subPrefixTemp + "." + valueColumn + " ");
					subquerySql.append(subquerySqlTemp);
					subquerySql.append(" )");
				}

				redundantIdx++;
			}
		}

		// 字段查询
		for (Map.Entry<String, EMSColumnAnalysis> columnEntry : columnMap.entrySet()) {
			String propertyName = columnEntry.getKey();
			EMSColumnAnalysis columnAnalysis = columnEntry.getValue();
			buildWhereSql(whereSql, paramList, isSplit, object, propertyName,
					" and " + prefixTemp + columnAnalysis.getName(), monthRange);
		}

		int embeddedIdxTemp = 0;
		// 内嵌子查询
		for (Map.Entry<String, EMSEmbeddedAnalysis> embeddedEntry : embeddedMap.entrySet()) {

			String propertyName = embeddedEntry.getKey();
			EMSEmbeddedAnalysis embeddedAnalysis = embeddedEntry.getValue();

			PropertyDescriptor property = new PropertyDescriptor(propertyName, object.getClass());
			Method readMethod = property.getReadMethod();

			List<T> list = (List<T>) readMethod.invoke(object);
			if (list != null && list.size() > 0) {

				String embeddedEntity = embeddedAnalysis.getEmbeddedEntity();
				EMSEntityAnalysis embeddedEntityAnalysis = analysis.getEntityMap().get(embeddedEntity);
				String foreignKey = embeddedAnalysis.getForeignKey();
				String embeddedTableName = getTableName(getObject((Class<T>) (embeddedEntityAnalysis.getCls()),
						object.getBuildingForContainer(), object.getSplitTimeType(), object.getSplitEnergyType()));

				// 子查询
				String subPrefixTemp = subPrefix + "_" + (level + 1) + "_" + embeddedIdxTemp;
				whereSql.append(" and " + prefixTemp + tableAnalysis.getPrimaryKey() + " in ( ");
				whereSql.append(" select ");
				whereSql.append(subPrefixTemp + "." + foreignKey);
				whereSql.append(" from ");
				if (!"".equals(embeddedEntityAnalysis.getTable().getSchema())) {
					whereSql.append(embeddedEntityAnalysis.getTable().getSchema() + "." + embeddedTableName + " "
							+ subPrefixTemp);
				} else {
					whereSql.append(embeddedTableName + " " + subPrefixTemp);
				}
				buildEMSCriteria(isSelect, (level + 1), embeddedIdxTemp, list.get(0), criteria);

				subquerySql.append(" )");

				embeddedIdxTemp++;

			}

		}

		whereSql.append(subquerySql);
		paramList.addAll(subqueryParamList);

		String tableName = getTableName(object);
		List<String> tableList = criteria.getTable().getTableNameList();

		if (level == 0) {
			if (isSplit) {
				tableList.addAll(EmsMonthThread.getTableList(tableName));
				if (isDesc) {
					Collections.reverse(tableList);
				}
				skip(tableList, monthRange);
			} else {
				tableList.add(tableName);
			}
		}

	}

	public static Boolean isSkip(Date month, EMSMonthRange monthRange) {

		Boolean flag = false;

		if (monthRange.getEqualList() != null && monthRange.getEqualList().size() > 0) {
			for (Date dateEqual : monthRange.getEqualList()) {
				int equalFlag = DateUtils.truncate(dateEqual, Calendar.MONTH).compareTo(month);
				if (equalFlag == 0) {
					flag = false;
					break;
				} else {
					flag = true;
				}
			}
		} else {
			Date now = new Date();
			if (monthRange.getGreaterDate() == null) {
				monthRange.setGreaterDate(DateUtils.addYears(now, -100));
			}
			if (monthRange.getLessDate() == null) {
				monthRange.setLessDate(DateUtils.addYears(now, 100));
			}
			if (monthRange.getGreaterOperator() == null) {
				monthRange.setGreaterOperator(SpecialOperator.$gte);
			}
			if (monthRange.getLessOperator() == null) {
				monthRange.setLessOperator(SpecialOperator.$lte);
			}

			int greaterFlag = month.compareTo(DateUtils.truncate(monthRange.getGreaterDate(), Calendar.MONTH));
			int lessFlag = month.compareTo(monthRange.getLessDate());

			if (greaterFlag >= 0 && lessFlag <= 0) {
				flag = false;
			} else {
				flag = true;
			}
		}
		return flag;
	}

	public static void skip(List<String> tableList, EMSMonthRange monthRange) throws Exception {
		SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyyMM");
		List<String> removeList = new ArrayList<String>();
		for (String table : tableList) {
			String source = table.split("_")[0];
			Date month = yyyyMM.parse(source);
			Boolean isSkip = isSkip(month, monthRange);
			if (isSkip) {
				removeList.add(table);
			}
		}
		tableList.removeAll(removeList);
	}

	public static <T extends BusinessObject> String getTableName(T object) {

		Class cls = object.getClass();
		Table table = (Table) cls.getAnnotation(Table.class);
		String name = table.name();
		String tableName = name;

		if (object.getBuildingForContainer() != null && !"".equals(object.getBuildingForContainer())) {
			tableName = tableName + "_" + object.getBuildingForContainer();
		}
		if (object.getSplitEnergyType() != null) {
			tableName = tableName + "_e" + object.getSplitEnergyType();
		}
		if (object.getSplitTimeType() != null) {
			tableName = tableName + "_t" + object.getSplitTimeType();
		}

		return tableName;
	}

	public static <T extends BusinessObject> T getObject(Class<T> cls, String building, Integer timeType,
			Integer energyType) throws Exception {
		BusinessObject businessObject = cls.newInstance();
		Dimension dimension = (Dimension) cls.getAnnotation(Dimension.class);
		if (dimension.dimension() == EMSDimension.Building) {
			businessObject.setBuildingForContainer(building);
		} else if (dimension.dimension() == EMSDimension.BuildingTime) {
			businessObject.setBuildingForContainer(building);
			businessObject.setSplitTimeType(timeType);
		} else if (dimension.dimension() == EMSDimension.BuildingEnergyTime) {
			businessObject.setBuildingForContainer(building);
			businessObject.setSplitTimeType(timeType);
			businessObject.setSplitEnergyType(energyType);
		}
		return (T) businessObject;
	}

	private <T extends BusinessObject> void buildWhereSql(StringBuffer whereSql, List<Object> paramList,
			Boolean isSplit, T object, String propertyName, String column, EMSMonthRange range) throws Exception {

		PropertyDescriptor property = new PropertyDescriptor(propertyName, object.getClass());
		Method readMethod = property.getReadMethod();

		Object value = null;
		Object temp = readMethod.invoke(object);

		if (temp != null && temp instanceof Date) {
			java.sql.Timestamp date = new java.sql.Timestamp(((Date) temp).getTime());
			value = date;
			if (isSplit && range != null) {
				range.getEqualList().add((Date) temp);
			}
		} else if (temp != null && temp instanceof Boolean) {
			if ((Boolean) temp) {
				value = 1;
			} else {
				value = 0;
			}
		} else {
			value = temp;
		}

		if (value != null) {
			// 有值为普通查询
			whereSql.append(column + " = ? ");
			paramList.add(value);
		} else {
			// 无值检查是否有特殊查询
			List<SpecialOperation> list = object.getSpecialOperateMap().get(property.getName());
			if (list != null && list.size() > 0) {
				// 特殊操作集合
				for (SpecialOperation operation : list) {
					// 特殊操作
					SpecialOperator specialOperator = operation.getSpecialOperator();
					whereSql.append(column + specialOperator.getOperator());

					if (specialOperator == SpecialOperator.$nin || specialOperator == SpecialOperator.$in) {
						// 如果特殊操作为 in 或者 not in 处理
						whereSql.append(" (");
						int idx = 0;
						List valueList = List.class.cast(operation.getValue());
						for (Object obj : valueList) {

							if (obj != null && obj instanceof Date) {
								java.sql.Timestamp date = new java.sql.Timestamp(((Date) obj).getTime());
								value = date;
								if (isSplit && specialOperator == SpecialOperator.$in && range != null) {
									range.getEqualList().add((Date) obj);
								}
							} else if (obj != null && obj instanceof Boolean) {
								if ((Boolean) obj) {
									value = 1;
								} else {
									value = 0;
								}
							} else {
								value = obj;
							}

							paramList.add(value);
							if (idx != 0) {
								whereSql.append(",");
							}
							whereSql.append("?");
							idx++;

						}
						whereSql.append(") ");
					} else if (specialOperator == SpecialOperator.$null
							|| specialOperator == SpecialOperator.$not_null) {
						// 空或者非空查询无占位符
					} else if (specialOperator == SpecialOperator.$exists) {
						if (operation.getValue().equals(true)) {
							whereSql.append(" not null ");
						} else {
							whereSql.append(" null ");
						}
					} else {
						// 普通操作处理
						whereSql.append(" ? ");

						Object obj = operation.getValue();

						if (obj != null && obj instanceof Date) {
							java.sql.Timestamp date = new java.sql.Timestamp(((Date) obj).getTime());
							value = date;
							if (isSplit) {
								if (specialOperator == SpecialOperator.$gte || specialOperator == SpecialOperator.$gt) {
									if (range != null) {
										range.setGreaterOperator(specialOperator);
										range.setGreaterDate((Date) obj);
									}
								}
								if (specialOperator == SpecialOperator.$lte || specialOperator == SpecialOperator.$lt) {
									if (range != null) {
										range.setLessOperator(specialOperator);
										range.setLessDate((Date) obj);
									}
								}
							}
						} else if (obj != null && obj instanceof Boolean) {
							if ((Boolean) obj) {
								value = 1;
							} else {
								value = 0;
							}
						} else {
							value = obj;
						}

						paramList.add(value);
					}

				}
			}
		}
	}

	public <T extends BusinessObject> void buildEMSUpdate(T object, EMSUpdate update) throws Exception {

		if (object == null) {
			throw new Exception("查询对象模板不能为空！");
		}

		if (!object.check()) {
			throw new Exception("查询对象模板不能为附加结构错误！");
		}

		StringBuffer setSql = update.getSetSql();
		setSql.append(" set ");

		List<Object> paramList = update.getParamList();

		Class cls = object.getClass();
		Entity entity = (Entity) cls.getAnnotation(Entity.class);

		EMSEntityAnalysis entityAnalysis = analysis.getEntityMap().get(entity.name());
		EMSTableAnalysis tableAnalysis = entityAnalysis.getTable();
		Map<String, EMSColumnAnalysis> columnMap = tableAnalysis.getColumnMap();
		boolean isSplit = false;
		// 按月分表精确查询参数
		EMSMonthRange monthRange = new EMSMonthRange();
		// 按月分表
		if (tableAnalysis.getMonth() != null && !"".equals(tableAnalysis.getMonth())
				&& systemSetting.getJdbcDriverClass().equals(JdbcDriverClass.MYSQL)) {
			isSplit = true;
			monthRange.setEqualList(new ArrayList<Date>());
		}

		int mark = 0;
		//
		for (Map.Entry<String, EMSColumnAnalysis> columnEntry : columnMap.entrySet()) {
			String propertyName = columnEntry.getKey();
			EMSColumnAnalysis columnAnalysis = columnEntry.getValue();

			PropertyDescriptor property = new PropertyDescriptor(propertyName, object.getClass());
			Method readMethod = property.getReadMethod();

			Object value = null;
			Object temp = readMethod.invoke(object);

			// date 转换
			if (temp != null && temp instanceof Date) {
				java.sql.Timestamp date = new java.sql.Timestamp(((Date) temp).getTime());
				value = date;
			} else if (temp != null && temp instanceof Boolean) {
				if ((Boolean) temp) {
					value = 1;
				} else {
					value = 0;
				}
			} else {
				value = temp;
			}

			if (value != null) {
				// 有值为普通操作
				if (mark != 0) {
					setSql.append(" , ");
				}
				setSql.append(columnAnalysis.getName() + " = ? ");
				paramList.add(value);
				mark++;

			} else {
				// 无值检查是否有特殊操作
				List<SpecialOperation> list = object.getSpecialOperateMap().get(property.getName());
				if (list != null && list.size() > 0) {
					// 特殊操作集合
					for (SpecialOperation operation : list) {
						// 特殊操作
						SpecialOperator specialOperator = operation.getSpecialOperator();

						if (specialOperator == SpecialOperator.$null
								|| (specialOperator == SpecialOperator.$exists && operation.getValue().equals(false))) {
							if (mark != 0) {
								setSql.append(" , ");
							}
							setSql.append(columnAnalysis.getName() + " = null ");
							mark++;
						}
					}
				}
			}
		}

		String tableName = getTableName(object);
		List<String> tableList = update.getTable().getTableNameList();

		if (isSplit) {
			tableList.addAll(EmsMonthThread.getTableList(tableName));
			skip(tableList, monthRange);
		} else {
			tableList.add(tableName);
		}

	}

	public <T extends BusinessObject> void buildEMSInsert(T object, EMSInsert insert) throws Exception {

		if (object == null) {
			throw new Exception("查询对象模板不能为空！");
		}

		if (!object.check()) {
			throw new Exception("查询对象模板不能为附加结构错误！");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String month = null;

		List<Object> paramList = insert.getParamList();
		StringBuffer insertIntoSql = insert.getInsertIntoSql();
		StringBuffer valuesSql = insert.getValuesSql();

		Class cls = object.getClass();
		Entity entity = (Entity) cls.getAnnotation(Entity.class);

		EMSEntityAnalysis entityAnalysis = analysis.getEntityMap().get(entity.name());
		EMSTableAnalysis tableAnalysis = entityAnalysis.getTable();
		Map<String, EMSColumnAnalysis> columnMap = tableAnalysis.getColumnMap();

		boolean isSplit = false;
		if (tableAnalysis.getMonth() != null && !"".equals(tableAnalysis.getMonth())
				&& systemSetting.getJdbcDriverClass().equals(JdbcDriverClass.MYSQL)) {
			isSplit = true;
		}

		int idx = 0;
		for (Map.Entry<String, EMSColumnAnalysis> columnEntry : columnMap.entrySet()) {
			String propertyName = columnEntry.getKey();
			EMSColumnAnalysis columnAnalysis = columnEntry.getValue();
			PropertyDescriptor property = new PropertyDescriptor(propertyName, object.getClass());
			Method readMethod = property.getReadMethod();

			Object value = null;
			Object temp = readMethod.invoke(object);

			if (temp == null) {
				continue;
			}

			if (temp instanceof Date) {
				if (isSplit && columnAnalysis.getName().equals(tableAnalysis.getMonth())) {
					month = sdf.format(temp);
				}
				java.sql.Timestamp date = new java.sql.Timestamp(((Date) temp).getTime());
				value = date;
			} else if (temp instanceof Boolean) {
				if ((Boolean) temp) {
					value = 1;
				} else {
					value = 0;
				}
			} else {
				value = temp;
			}

			if (idx == 0) {
				insertIntoSql.append("(" + columnAnalysis.getName());
				valuesSql.append("(?");
			} else {
				insertIntoSql.append("," + columnAnalysis.getName());
				valuesSql.append(",?");
			}

			paramList.add(value);

			idx++;
		}

		insertIntoSql.append(")");
		valuesSql.append(")");

		String tableName = getTableName(object);
		if (month != null) {
			EmsMonthThread.setTableList(tableName, month + "_" + tableName);
			tableName = month + "_" + tableName;
		}
		insert.setTable(tableName);
		insert.setSchema(tableAnalysis.getSchema());
	}

	public static String createTableSqlReplace(String createTableSql, String schemaName, String tableNameTemp) {
		// TODO Auto-generated method stub

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String totalTableName;
		if (!"".equals(schemaName)) {
			totalTableName = (schemaName + "." + tableNameTemp).toLowerCase();
		} else {
			totalTableName = tableNameTemp.toLowerCase();
		}
		String index = "idx" + sdf.format(new Date()) + "_" + random.nextInt(1000);
		return createTableSql.replace(SystemConstant.REPLACE_TABLE, totalTableName)
				.replace(SystemConstant.REPLACE_INDEX, index);
	}

	public static <T extends BusinessObject> T recursiveObject(T object, Map<String, Object> criteria)
			throws Exception {

		SimpleDateFormat standard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Class cls = object.getClass();
		BeanInfo bi = Introspector.getBeanInfo(cls);
		PropertyDescriptor[] propertyList = bi.getPropertyDescriptors();

		for (Map.Entry<String, Object> entryCriteria : criteria.entrySet()) {
			// json字段
			String criteriaKey = entryCriteria.getKey();
			// json字段值
			Object value = entryCriteria.getValue();

			for (PropertyDescriptor property : propertyList) {
				// 属性类型
				Class propertyClass = property.getPropertyType();
				// 属性的json属性
				Field fieldJson = null;
				try {
					fieldJson = cls.getDeclaredField(property.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				JsonProperty jp = null;
				if (fieldJson != null) {
					jp = (JsonProperty) fieldJson.getAnnotation(JsonProperty.class);
				}
				if (jp != null) {
					// 找到json属性
					if (jp.value().equalsIgnoreCase(criteriaKey)) {
						// list属性
						if (propertyClass == List.class) {
							// 级联属性
							Type type = fieldJson.getGenericType();
							if (type instanceof ParameterizedType) {
								Class genericClazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
								Object obj = genericClazz.newInstance();
								if (obj instanceof BusinessObject) {

									((BusinessObject) obj).setBuildingForContainer(object.getBuildingForContainer());
									((BusinessObject) obj).setSplitEnergyType(object.getSplitEnergyType());
									((BusinessObject) obj).setSplitTimeType(object.getSplitTimeType());

									Map<String, Object> criteriaSub = (Map<String, Object>) ((Map) value)
											.get(Operator.$ELEMMATCH);

									Method writeMethod = property.getWriteMethod();
									List subList = new ArrayList();
									subList.add(recursiveObject((BusinessObject) obj, criteriaSub));
									writeMethod.invoke(object, subList);

									break;
								}
							}

						} else {
							// 普通属性
							if (value != null) {
								// 值非空
								if (value instanceof Map) {
									// 复杂查询
									Map<String, Object> mapValue = (Map<String, Object>) value;
									for (Map.Entry<String, Object> entryValue : mapValue.entrySet()) {
										Object temp = entryValue.getValue();
										if (propertyClass == Date.class) {
											if (temp instanceof List) {
												List<Date> dateList = new ArrayList<Date>();
												for (String dateStr : (List<String>) temp) {
													Date date = standard.parse(dateStr);
													dateList.add(date);
												}
												temp = dateList;
											} else {
												// 时间
												Date date = standard.parse((String) temp);
												temp = date;
											}
										} else if (propertyClass == Double.class && !(temp instanceof Double)) {
											// Object to Double
											if (temp instanceof List) {
												List<Double> numList = new ArrayList<Double>();
												for (Object num : (List) temp) {
													numList.add(new Double(num.toString()));
												}
												temp = numList;
											} else {
												temp = new Double(temp.toString());
											}
										}
										SpecialOperator specialOperator = Operator
												.getSpecialOperator(entryValue.getKey());
										if (specialOperator != null) {
											object.setSpecialOperation(property.getName(), specialOperator, temp);
										} else {
											throw new RuntimeException(" not support operator ");
										}
									}
									break;
								} else {
									// 普通查询
									Object temp = value;
									if (propertyClass == Date.class) {
										// 时间
										Date date = standard.parse((String) temp);
										temp = date;
									} else if (propertyClass == Double.class && !(temp instanceof Double)) {
										// Object to Double
										temp = new Double(temp.toString());
									}

									Method writeMethod = property.getWriteMethod();
									writeMethod.invoke(object, temp);
									break;
								}
							} else {
								// 值空
								object.setSpecialOperation(property.getName(), SpecialOperator.$null, null);
								break;
							}
						}
					} else {
						int index = criteriaKey.indexOf(".");
						if (index != -1) {
							String tempField = criteriaKey.substring(0, index);
							String nextCascade = criteriaKey.substring(index + 1);
							if (jp.value().equalsIgnoreCase(tempField) && propertyClass == List.class) {
								// 级联属性
								Type type = fieldJson.getGenericType();
								if (type instanceof ParameterizedType) {
									Class genericClazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
									Object obj = genericClazz.newInstance();
									if (obj instanceof BusinessObject) {

										((BusinessObject) obj)
												.setBuildingForContainer(object.getBuildingForContainer());
										((BusinessObject) obj).setSplitEnergyType(object.getSplitEnergyType());
										((BusinessObject) obj).setSplitTimeType(object.getSplitTimeType());

										Map<String, Object> criteriaSub = new LinkedHashMap<String, Object>();
										criteriaSub.put(nextCascade, value);

										Method writeMethod = property.getWriteMethod();
										List subList = new ArrayList();
										subList.add(recursiveObject((BusinessObject) obj, criteriaSub));
										writeMethod.invoke(object, subList);

										break;
									}
								}
							}
						}
					}
				}
			}
		}

		return object;
	}

}
