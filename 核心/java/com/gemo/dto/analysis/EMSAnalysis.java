package com.gemo.dto.analysis;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.annotation.Column;
import com.gemo.annotation.Entity;
import com.gemo.annotation.ForeignKey;
import com.gemo.annotation.Id;
import com.gemo.annotation.Index;
import com.gemo.annotation.Month;
import com.gemo.annotation.Order;
import com.gemo.annotation.Orders;
import com.gemo.annotation.Property;
import com.gemo.annotation.Redundant;
import com.gemo.annotation.Table;
import com.gemo.constant.SystemConstant;
import com.gemo.constant.SystemSetting;
import com.gemo.enumeration.EMSOrder;
import com.gemo.mvc.dialect.DatabaseDialect;
import com.gemo.mvc.pojo.BusinessObject;
import com.gemo.utils.PackageScanUtils;

/**
 * EMS ORM解析
 */
@Component
@SuppressWarnings("unchecked")
public class EMSAnalysis {

	private static Logger log = Logger.getLogger(EMSAnalysis.class);

	private static final String packageScan = "com.persagy.ems.pojo";

	@Resource
	private SystemSetting systemSetting;

	// EMS ORM 对象
	private static final Set<Class<?>> classSet = PackageScanUtils.getClasses(packageScan);

	public static Set<Class<?>> getClassSet() {
		return classSet;
	}

	// 实体Map
	private final Map<String, EMSEntityAnalysis> entityMap = new HashMap<String, EMSEntityAnalysis>();

	public Map<String, EMSEntityAnalysis> getEntityMap() {
		return entityMap;
	}

	public EMSEntityAnalysis getEMSEntityAnalysis(String entityName) {
		return entityMap.get(entityName);
	}

	public EMSEntityAnalysis getEMSEntityAnalysis(Class<?> cls) {
		Entity entity = (Entity) cls.getAnnotation(Entity.class);
		if (entity == null) {
			return null;
		}
		return entityMap.get(entity.name());
	}

	// 关键字列表
	private Map<String, Map<String, Boolean>> keywordMap;

	/**
	 * ORM对象分析
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	private void init() throws Exception {

		log.info("********* ORM对象分析开始 ********");

		URL url = EMSAnalysis.class.getResource("keyword.json");
		try {
			setKeywordMap(SystemConstant.jsonMapper.readValue(url, Map.class));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		DatabaseDialect databaseDialect = (DatabaseDialect) systemSetting.getContext()
				.getBean(systemSetting.getJdbcDriverClass());

		for (Class<?> cls : classSet) {
			// 实体注解
			Entity entity = cls.getAnnotation(Entity.class);
			if (entity == null) {
				// 无实体注解，不处理
				continue;
			}

			log.info("ORM对象分析：" + cls.getName());
			// 实体分析
			EMSEntityAnalysis emsEntityAnalysis = new EMSEntityAnalysis();
			// 实体名称
			emsEntityAnalysis.setName(entity.name());
			emsEntityAnalysis.setCls(cls);
			// 表注解
			Table table = cls.getAnnotation(Table.class);
			if (table == null) {
				// 如果表注解为空则抛出配置异常
				throw new RuntimeException(cls.getName() + "未配置@Table");
			}

			for (Map.Entry<String, Map<String, Boolean>> databaseEntry : keywordMap.entrySet()) {
				Boolean isKeyword = databaseEntry.getValue().get(table.name().toLowerCase());
				if (isKeyword != null && isKeyword) {
					throw new RuntimeException("表 " + table.name() + " 为 " + databaseEntry.getKey() + " 关键字！");
				}
			}

			// 表分析
			EMSTableAnalysis emsTableAnalysis = new EMSTableAnalysis();
			// schema
			emsTableAnalysis.setSchema(databaseDialect.getRealSchema(cls));
			// 表名
			emsTableAnalysis.setName(table.name());
			// 表注释
			emsTableAnalysis.setComment(table.comment());
			// 表是否支持事务
			emsTableAnalysis.setTransaction(table.transaction());
			// 表是否已创建（暂时未用到）
			emsTableAnalysis.setCreated(false);
			emsEntityAnalysis.setTable(emsTableAnalysis);

			Month month = cls.getAnnotation(Month.class);
			if (month != null) {
				// 分表字段
				emsTableAnalysis.setMonth(month.column());
			}

			// 内嵌字段map
			Map<String, EMSEmbeddedAnalysis> embeddedMap = new LinkedHashMap<String, EMSEmbeddedAnalysis>();
			// 冗余字段map
			Map<String, EMSRedundantAnalysis> redundantMap = new LinkedHashMap<String, EMSRedundantAnalysis>();
			// 数据库字段map
			Map<String, EMSColumnAnalysis> columnMap = new LinkedHashMap<String, EMSColumnAnalysis>();

			// 字段解析
			BeanInfo bi = Introspector.getBeanInfo(cls);
			PropertyDescriptor[] propertyDescriptorArray = bi.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptorArray) {
				// 类属性名
				String propertyName = propertyDescriptor.getName();
				// JavaBean属性
				Field field = null;
				Class<?> tempClass = cls;
				while (tempClass != null) {
					try {
						field = tempClass.getDeclaredField(propertyName);
						break;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						tempClass = tempClass.getSuperclass();
					}
				}

				if (field == null) {
					// JavaBean属性未找到，例如：class
					continue;
				}

				// 冗余注解
				Redundant redundant = field.getAnnotation(Redundant.class);
				if (redundant != null) {
					// 关联实体注解
					Entity mainEntity = redundant.cls().getAnnotation(Entity.class);
					// 冗余字段显示名
					JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
					// 冗余分析
					EMSRedundantAnalysis redundantAnalysis = new EMSRedundantAnalysis();
					// 外键字段（暂存属性名，后续程序处理成真实字段名）
					redundantAnalysis.setForeignColumn(redundant.field());
					// 关联实体名
					redundantAnalysis.setMainEntity(mainEntity.name());
					// 冗余字段显示名
					redundantAnalysis.setRedundantName(jsonProperty.value());
					// 关联实体显示值字段（暂存属性名，后续程序处理成真实字段名）
					redundantAnalysis.setValueColumn(redundant.valueField());

					redundantMap.put(propertyName, redundantAnalysis);

				} else {
					// 从JavaBean属性获取字段注解
					Column column = field.getAnnotation(Column.class);
					if (column == null) {
						// JavaBean属性读方法
						Method readMehtod = propertyDescriptor.getReadMethod();
						// JavaBean属性读方法获取字段注解
						column = readMehtod.getAnnotation(Column.class);
						if (column == null) {
							if (propertyDescriptor.getPropertyType() == List.class) {
								// 内嵌属性，获取JavaBean参数泛型
								Type type = field.getGenericType();
								if (type instanceof ParameterizedType) {
									// 获取泛型Class
									Class<?> genericClass = (Class<?>) ((ParameterizedType) type)
											.getActualTypeArguments()[0];
									if (genericClass.newInstance() instanceof BusinessObject) {
										// Class为EMS ORM 对象，内嵌解析
										EMSEmbeddedAnalysis embeddedAnalysis = new EMSEmbeddedAnalysis();
										// 附加属性注解
										Property property = field.getAnnotation(Property.class);
										if (property != null) {
											// 内嵌为附加属性
											embeddedAnalysis.setIsProperty(true);
										}
										Orders orders = field.getAnnotation(Orders.class);
										if (orders != null) {
											Map<String, EMSOrder> orderMap = new LinkedHashMap<String, EMSOrder>();
											embeddedAnalysis.setOrderMap(orderMap);
											for (Order order : orders.orders()) {
												orderMap.put(order.field(), order.order());
											}
										}
										// 内嵌关联实体注解
										Entity mainEntity = genericClass.getAnnotation(Entity.class);
										// 内嵌关联实体名称
										embeddedAnalysis.setEmbeddedEntity(mainEntity.name());
										embeddedMap.put(propertyName, embeddedAnalysis);
									}
								}
							}
							// 如果未获取到字段注解则，跳过后续处理
							continue;
						}
					}
					// 主键注解
					Id id = field.getAnnotation(Id.class);
					if (id != null) {
						// 主键真实字段
						emsTableAnalysis.setPrimaryKey(column.name());
						emsTableAnalysis.setPrimaryKeyProperty(propertyName);
					}

					for (Map.Entry<String, Map<String, Boolean>> databaseEntry : keywordMap.entrySet()) {
						Boolean isKeyword = databaseEntry.getValue().get(column.name().toLowerCase());
						if (isKeyword != null && isKeyword) {
							throw new RuntimeException(
									table.name() + " 表的字段 " + column.name() + " 为 " + databaseEntry.getKey() + " 关键字！");
						}
					}

					// 字段解析
					EMSColumnAnalysis emsColumnAnalysis = new EMSColumnAnalysis();
					//
					emsColumnAnalysis.setPropertyDescriptor(propertyDescriptor);
					// 字段排序
					emsColumnAnalysis.setOrder(column.order());
					// 字段名称
					emsColumnAnalysis.setName(column.name());
					// 字段长度
					emsColumnAnalysis.setLength(column.length());
					// 字段类型
					emsColumnAnalysis.setType(databaseDialect.getDatabaseColumnType(
							propertyDescriptor.getPropertyType(), column.length(), column.scale()));
					// 字段（小数位数）
					emsColumnAnalysis.setScale(column.scale());
					// 字段注释
					emsColumnAnalysis.setComment(column.comment());
					// 字段是否允许为空
					emsColumnAnalysis.setNullable(column.nullable());
					// 字段是否已创建（暂时未用到）
					emsColumnAnalysis.setCreated(false);

					// 外键注解
					ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
					if (foreignKey != null) {
						// 外键关联实体
						Entity mainEntity = foreignKey.mainClass().getAnnotation(Entity.class);
						// 外键实体名
						emsColumnAnalysis.setMainEntity(mainEntity.name());
					}

					columnMap.put(propertyName, emsColumnAnalysis);

				}

			}

			if (columnMap.size() == 0) {
				// 未配置过字段，抛出异常
				throw new RuntimeException(cls.getName() + "未配置@Column");
			}
			emsTableAnalysis.setColumnMap(columnMap);
			emsTableAnalysis.setRedundantMap(redundantMap);
			emsTableAnalysis.setEmbeddedMap(embeddedMap);

			// 索引解析
			List<EMSIndexAnalysis> indexList = new ArrayList<EMSIndexAnalysis>();
			for (Index index : table.indexes()) {
				if (index.columns().length == 0) {
					// 索引未配置字段
					throw new RuntimeException(cls.getName() + "配置@Index必须包含至少一个字段");
				}
				List<String> columnList = Arrays.asList(index.columns());
				// 索引解析
				EMSIndexAnalysis emsIndexAnalysis = new EMSIndexAnalysis();
				// 是否唯一
				emsIndexAnalysis.setUnique(index.unique());
				// 索引字段列表
				emsIndexAnalysis.setColumnList(columnList);
				// 索引是否已创建（暂时未用）
				emsIndexAnalysis.setCreated(false);
				indexList.add(emsIndexAnalysis);
			}

			emsTableAnalysis.setIndexList(indexList);

			entityMap.put(entity.name(), emsEntityAnalysis);

		}

		for (Map.Entry<String, EMSEntityAnalysis> entityEntry : entityMap.entrySet()) {
			EMSEntityAnalysis entityAnalysis = entityEntry.getValue();
			EMSTableAnalysis tableAnalysis = entityAnalysis.getTable();
			// 处理冗余字段未解析完的部分
			for (Map.Entry<String, EMSRedundantAnalysis> redundantEntity : tableAnalysis.getRedundantMap().entrySet()) {
				EMSRedundantAnalysis redundantAnalysis = redundantEntity.getValue();
				// 关联表值字段
				String valueColumn = entityMap.get(redundantAnalysis.getMainEntity()).getTable().getColumnMap()
						.get(redundantAnalysis.getValueColumn()).getName();
				redundantAnalysis.setValueColumn(valueColumn);
				// 外键字段
				String foreignColumn = tableAnalysis.getColumnMap().get(redundantAnalysis.getForeignColumn()).getName();
				redundantAnalysis.setForeignColumn(foreignColumn);
			}
			// 处理内嵌字段未解析完部分
			for (Map.Entry<String, EMSEmbeddedAnalysis> embeddedEntry : tableAnalysis.getEmbeddedMap().entrySet()) {
				// 内嵌实体
				EMSEmbeddedAnalysis embeddedAnalysis = embeddedEntry.getValue();
				// 内嵌实体名
				String embeddedEntity = embeddedAnalysis.getEmbeddedEntity();
				// 内嵌实体属性字段集合
				for (Map.Entry<String, EMSColumnAnalysis> columnEntry : entityMap.get(embeddedEntity).getTable()
						.getColumnMap().entrySet()) {
					// 内嵌实体属性字段
					EMSColumnAnalysis columnAnalysis = columnEntry.getValue();
					if (entityAnalysis.getName().equals(columnAnalysis.getMainEntity())) {
						// 字段实体与主实体相同
						embeddedAnalysis.setForeignKey(columnAnalysis.getName());
						embeddedAnalysis.setForeignKeyProperty(columnEntry.getKey());
					}
				}
			}
		}

		log.info("********* ORM对象分析结束 ********");

		databaseDialect.init();
	}

	public Map<String, Map<String, Boolean>> getKeywordMap() {
		return keywordMap;
	}

	private void setKeywordMap(Map<String, Map<String, Boolean>> keywordMap) {
		this.keywordMap = keywordMap;
	}
}
