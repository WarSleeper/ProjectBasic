package com.gemo.dto.interpreter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.constant.EMSConstant;
import com.gemo.constant.EMSConstant.Structure;

public class DataType {

	// 数据标识
	@JsonProperty(Structure.MARK)
	private String mark;
	// 数据对象
	@JsonProperty(Structure.COLLECTION)
	private String collection;
	// 时间类型
	@JsonProperty(EMSConstant.DataType.TIME_TYPE)
	private Integer timeType;
	// 能耗类型
	@JsonProperty(EMSConstant.DataType.ENERGY_TYPE_ID)
	private Integer energyTypeId;

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}

	public Integer getEnergyTypeId() {
		return energyTypeId;
	}

	public void setEnergyTypeId(Integer energyTypeId) {
		this.energyTypeId = energyTypeId;
	}

	@Override
	public String toString() {
		return "DataType [collection=" + collection + ", energyTypeId=" + energyTypeId + ", mark=" + mark
				+ ", timeType=" + timeType + "]";
	}

}
