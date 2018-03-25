package com.gemo.mvc.pojo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gemo.annotation.Dimension;
import com.gemo.annotation.Property;
import com.gemo.constant.SystemConstant;
import com.gemo.enumeration.EMSDimension;
import com.gemo.enumeration.EMSOrder;
import com.gemo.enumeration.SpecialOperator;

/**
 * 业务对象基类
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class BusinessObject implements Serializable {

	private static final long serialVersionUID = 1L;

	// 跳过记录数
	// @JsonIgnore
	private Long skip;
	// 检索记录数
	// @JsonIgnore
	private Long limit;
	// 建筑分表
	// @JsonIgnore
	private String buildingForContainer;
	// 时间类型
	// @JsonIgnore
	private Integer splitTimeType;
	// 能耗类型
	// @JsonIgnore
	private Integer splitEnergyType;
	// 字段单表特殊操作
	// @JsonIgnore
	private Map<String, List<SpecialOperation>> specialOperateMap = new LinkedHashMap<String, List<SpecialOperation>>();
	// 排序
	// @JsonIgnore
	private Map<String, EMSOrder> sortMap = new LinkedHashMap<String, EMSOrder>();
	// 扩展字段
	// @JsonIgnore
	private Map<String, String> extensionMap = new LinkedHashMap<String, String>();

	public void setSpecialOperateMap(Map<String, List<SpecialOperation>> specialOperateMap) {
		this.specialOperateMap = specialOperateMap;
	}

	public void setSortMap(Map<String, EMSOrder> sortMap) {
		this.sortMap = sortMap;
	}

	public void setExtensionMap(Map<String, String> extensionMap) {
		this.extensionMap = extensionMap;
	}

	public Long getSkip() {
		return skip;
	}

	public void setSkip(Long skip) {
		this.skip = skip;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public Map<String, EMSOrder> getSortMap() {
		return sortMap;
	}

	public Integer getSplitTimeType() {
		return splitTimeType;
	}

	public void setSplitTimeType(Integer splitTimeType) {
		this.splitTimeType = splitTimeType;
	}

	public Integer getSplitEnergyType() {
		return splitEnergyType;
	}

	public void setSplitEnergyType(Integer splitEnergyType) {
		this.splitEnergyType = splitEnergyType;
	}

	public void setBuildingForContainer(String buildingForContainer) {
		if (buildingForContainer != null) {
			buildingForContainer = buildingForContainer.toLowerCase();
		}
		this.buildingForContainer = buildingForContainer;
	}

	public String getBuildingForContainer() {
		return buildingForContainer;
	}

	public Map<String, List<SpecialOperation>> getSpecialOperateMap() {
		return specialOperateMap;
	}

	public Map<String, String> getExtensionMap() throws Exception {
		if (extensionMap.size() == 0) {
			Class cls = this.getClass();
			BeanInfo bi = Introspector.getBeanInfo(cls);
			PropertyDescriptor[] propertyList = bi.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyList) {
				String propertyName = property.getName();
				Method readMethod = property.getReadMethod();
				try {
					Field field = cls.getDeclaredField(propertyName);
					Property propertyAnnotation = field.getAnnotation(Property.class);
					if (propertyAnnotation != null) {
						List<BusinessObject> list = (List<BusinessObject>) readMethod.invoke(this);
						if (list == null || list.size() == 0) {
							break;
						}
						for (BusinessObject propertyObject : list) {
							PropertyDescriptor propertyNameProperty = new PropertyDescriptor(
									propertyAnnotation.propertyNameField(), propertyObject.getClass());
							PropertyDescriptor propertyValueProperty = new PropertyDescriptor(
									propertyAnnotation.propertyValueField(), propertyObject.getClass());
							Method readMethodName = propertyNameProperty.getReadMethod();
							Method readMethodValue = propertyValueProperty.getReadMethod();
							String propertyNameValue = (String) readMethodName.invoke(propertyObject);
							String propertyValueValue = (String) readMethodValue.invoke(propertyObject);
							extensionMap.put(propertyNameValue, propertyValueValue);
						}
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
			}

		}
		return extensionMap;
	}

	/**
	 * 设置排序
	 * 
	 * @param fieldName
	 *            对象字段
	 * @param order
	 *            排序类型
	 */
	public void setSort(String fieldName, EMSOrder order) {

		Boolean isFind = false;
		try {
			BeanInfo bi = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] propertyList = bi.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyList) {
				String realFieldName = propertyDescriptor.getName();
				if (realFieldName.equalsIgnoreCase(fieldName)) {
					fieldName = realFieldName;
					isFind = true;
				}
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage());
		}

		if (!isFind) {
			return;
		}

		sortMap.put(fieldName, order);
	}

	/**
	 * 设置特殊操作
	 * 
	 * @param fieldName
	 *            对象字段
	 * @param operator
	 *            特殊操作
	 * @param value
	 *            特殊操作值
	 */
	public void setSpecialOperation(String fieldName, SpecialOperator operator, Object value) {

		Boolean isFind = false;
		try {
			BeanInfo bi = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] propertyList = bi.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyList) {
				String realFieldName = propertyDescriptor.getName();
				if (realFieldName.equalsIgnoreCase(fieldName)) {
					fieldName = realFieldName;
					isFind = true;
				}
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage());
		}

		if (!isFind) {
			return;
		}

		List<SpecialOperation> list = specialOperateMap.get(fieldName);
		if (list == null) {
			list = new ArrayList<SpecialOperation>();
			specialOperateMap.put(fieldName, list);
		}
		SpecialOperation operation = new SpecialOperation();
		operation.setSpecialOperator(operator);
		operation.setValue(value);
		list.add(operation);
	}

	/**
	 * 数据维度检查
	 * 
	 * @return
	 */
	public final Boolean check() {
		Boolean flag = true;
		Class cls = this.getClass();
		Dimension emsDimension = (Dimension) cls.getAnnotation(Dimension.class);
		if (emsDimension != null) {
			EMSDimension dimension = emsDimension.dimension();
			if (dimension == EMSDimension.Building) {
				if (buildingForContainer == null || "".equals(buildingForContainer) || splitTimeType != null
						|| splitEnergyType != null) {
					flag = false;
				}
			} else if (dimension == EMSDimension.BuildingEnergyTime) {
				if (buildingForContainer == null || "".equals(buildingForContainer) || splitTimeType == null
						|| splitEnergyType == null) {
					flag = false;
				}
			} else if (dimension == EMSDimension.BuildingTime) {
				if (buildingForContainer == null || "".equals(buildingForContainer) || splitTimeType == null
						|| splitEnergyType != null) {
					flag = false;
				}
			} else if (dimension == EMSDimension.No) {
				if ((buildingForContainer != null && !"".equals(buildingForContainer)) || splitTimeType != null
						|| splitEnergyType != null) {
					flag = false;
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	public String buildId() {
		return UUID.randomUUID().toString();
	}

	public void setNull(Set<String> fieldSet) throws Exception {
		if (fieldSet == null) {
			return;
		}
		BeanInfo bi = Introspector.getBeanInfo(this.getClass(), BusinessObject.class);
		PropertyDescriptor[] propertyList = bi.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyList) {
			String propertyName = propertyDescriptor.getName();
			if (fieldSet.size() == 0 || fieldSet.contains(propertyName)) {
				Object value = propertyDescriptor.getReadMethod().invoke(this);
				if (value == null) {
					this.setSpecialOperation(propertyName, SpecialOperator.$exists, false);
				}
			}
		}
	}

	@Override
	public String toString() {

		String objectString = null;
		try {
			objectString = SystemConstant.jsonMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectString;
	}

}
