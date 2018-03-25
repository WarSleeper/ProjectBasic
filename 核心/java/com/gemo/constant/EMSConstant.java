package com.gemo.constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gemo.enumeration.SpecialOperator;

public class EMSConstant {

	// 接口操作符常量
	public static class Operator {
		// 正则
		public static final String $REGEX = "$regex";
		// 存在
		public static final String $EXISTS = "$exists";
		// 小于
		public static final String $LT = "$lt";
		// 小于等于
		public static final String $LTE = "$lte";
		// 大于
		public static final String $GT = "$gt";
		// 大于等于
		public static final String $GTE = "$gte";
		// 不等于
		public static final String $NE = "$ne";
		// 包含
		public static final String $IN = "$in";
		// 不包含
		public static final String $NIN = "$nin";
		// 子表（原mongodb内嵌文档）添加
		public static final String $ADDTOSET = "$addToSet";
		// 子表（原mongodb内嵌文档）删除
		public static final String $PULL = "$pull";
		// 子表（原mongodb内嵌文档）查询
		public static final String $ELEMMATCH = "$elemMatch";
		// 子表（原mongodb内嵌文档）分隔
		public static final String POINT = "\\.";
		// 子表（原mongodb内嵌文档）分隔
		public static final String $ = "\\.\\&\\.";
		// 查询操作支持
		private static final Set<String> criteriaSet = new HashSet<String>();
		static {
			criteriaSet.add($LT);
			criteriaSet.add($LTE);
			criteriaSet.add($GT);
			criteriaSet.add($GTE);
			criteriaSet.add($NE);
			criteriaSet.add($IN);
			criteriaSet.add($NIN);
			criteriaSet.add($ELEMMATCH);
		}

		public static Boolean supportCriteria(String operator) {
			return criteriaSet.contains(operator);
		}

		// 接口与内部操作对应
		private static final Map<String, SpecialOperator> specialOperatorMap = new HashMap<String, SpecialOperator>();
		static {
			specialOperatorMap.put($LT, SpecialOperator.$lt);
			specialOperatorMap.put($LTE, SpecialOperator.$lte);
			specialOperatorMap.put($GT, SpecialOperator.$gt);
			specialOperatorMap.put($GTE, SpecialOperator.$gte);
			specialOperatorMap.put($NE, SpecialOperator.$ne);
			specialOperatorMap.put($IN, SpecialOperator.$in);
			specialOperatorMap.put($NIN, SpecialOperator.$nin);
			specialOperatorMap.put(null, SpecialOperator.$null);
		}

		public static SpecialOperator getSpecialOperator(String operator) {
			return specialOperatorMap.get(operator);
		}
	}

	public static class Mark {
		// 调研
		public static final String EMS = "ems";
		// 数据管理
		public static final String UDM = "udm";
		// 附加
		public static final String EMSVDATA = "emsvdata";
		// 统计数据
		public static final String DATA = "data";
		// 租户
		public static final String TENANT = "tenant";
		// 计算流程
		public static final String WORKFLOW = "workflow";
		// 是否按楼分表
		private static final Map<String, Boolean> setBuildingMap = new HashMap<String, Boolean>();
		static {
			setBuildingMap.put(EMS, false);
			setBuildingMap.put(UDM, false);
			setBuildingMap.put(EMSVDATA, true);
			setBuildingMap.put(DATA, true);
			setBuildingMap.put(TENANT, false);
			setBuildingMap.put(WORKFLOW, false);
		}

		public static Boolean isSetBuilding(String mark) {
			Boolean value = setBuildingMap.get(mark);
			if (value == null) {
				value = false;
			}
			return value;
		}
	}

	// 接口结果常量
	public static class Result {
		// 版本
		public static final String VERSION = "version";
		// 结果
		public static final String RESULT = "result";
		// 内容
		public static final String CONTENT = "content";
		// 原因
		public static final String REASON = "reason";
		// 结果标识-成功
		public static final String SUCCESS = "success";
		// 结果标识-失败
		public static final String FAILURE = "failure";
	}

	public static class Type {
		// 区分建筑
		public static final String BUILDING = "building";
		// udm
		public static final String UDM = "udm";
		// 类型支持
		private static final Map<String, Boolean> typeMap = new HashMap<String, Boolean>();
		static {
			typeMap.put(BUILDING, true);
			typeMap.put(UDM, true);
		}

		public static Boolean isType(String type) {
			Boolean value = typeMap.get(type);
			if (value == null) {
				value = false;
			}
			return value;
		}
	}

	public static class DataType {
		// 时间类型
		public static final String TIME_TYPE = "timeType";
		// 能耗类型
		public static final String ENERGY_TYPE_ID = "energyTypeId";
	}

	public static class Operation {
		// 增
		public static final String INSERT = "insert";
		// 查
		public static final String SELECT = "select";
		// 统计数量
		public static final String COUNT = "count";
		// 删
		public static final String DELETE = "delete";
		// 改
		public static final String UPDATE = "update";
	}

	public static class Structure {
		// 数据标识
		public static final String MARK = "mark";
		// 数据对象
		public static final String COLLECTION = "collection";
		// 接口
		public static final String VIEW = "view";
		// 操作类型
		public static final String TYPE = "type";
		// 建筑标识
		public static final String BUILDING_SIGN = "buildingSign";
		// 数据类型
		public static final String DATA_TYPE = "dataType";
		// 操作类型
		public static final String OPERATION = "operation";
		// 查询对象键值对
		public static final String CRITERIA = "criteria";
		// 添加对象键值对
		public static final String INSERT_OBJ = "insertObj";
		// 更新对象键值对
		public static final String UPDATE_OBJ = "updateObj";
		// 限制
		public static final String LIMIT = "limit";
		// 跳过
		public static final String SKIP = "skip";
		// 排序
		public static final String SORT = "sort";
	}

}
