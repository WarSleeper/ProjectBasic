package com.gemo.dto.interpreter;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.constant.EMSConstant.Result;
import com.gemo.constant.EMSConstant.Structure;
import com.gemo.constant.EMSConstant.Type;

public class HandleJsonObject {

	// 版本
	@JsonProperty(Result.VERSION)
	private String version;
	// 数据类型
	@JsonProperty(Structure.TYPE)
	private String type;
	// 数据类型-区分建筑
	@JsonProperty(Type.BUILDING)
	private Building building;
	// 数据类型-UDM
	@JsonProperty(Type.UDM)
	private Udm udm;
	// 操作类型
	@JsonProperty(Structure.OPERATION)
	private String operation;
	// 查询对象
	@JsonProperty(Structure.CRITERIA)
	private Map<String, Object> criteria;
	// 新增对象
	@JsonProperty(Structure.INSERT_OBJ)
	private Map<String, Object> insertObj;
	// 更新对象
	@JsonProperty(Structure.UPDATE_OBJ)
	private Map<String, Object> updateObj;
	// 分页排序
	@JsonProperty(Structure.LIMIT)
	private Limit limit;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public Udm getUdm() {
		return udm;
	}

	public void setUdm(Udm udm) {
		this.udm = udm;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Map<String, Object> getCriteria() {
		return criteria;
	}

	public void setCriteria(Map<String, Object> criteria) {
		this.criteria = criteria;
	}

	public Map<String, Object> getInsertObj() {
		return insertObj;
	}

	public void setInsertObj(Map<String, Object> insertObj) {
		this.insertObj = insertObj;
	}

	public Map<String, Object> getUpdateObj() {
		return updateObj;
	}

	public void setUpdateObj(Map<String, Object> updateObj) {
		this.updateObj = updateObj;
	}

	public Limit getLimit() {
		return limit;
	}

	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "HandleJsonObject [building=" + building + ", criteria=" + criteria + ", insertObj=" + insertObj
				+ ", limit=" + limit + ", operation=" + operation + ", type=" + type + ", udm=" + udm + ", updateObj="
				+ updateObj + ", version=" + version + "]";
	}

}
