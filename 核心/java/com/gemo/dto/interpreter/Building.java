package com.gemo.dto.interpreter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.constant.EMSConstant.Structure;

public class Building {

	// 建筑标识
	@JsonProperty(Structure.BUILDING_SIGN)
	private String buildingSign;
	// 数据类型
	@JsonProperty(Structure.DATA_TYPE)
	private DataType dataType;

	public String getBuildingSign() {
		return buildingSign;
	}

	public void setBuildingSign(String buildingSign) {
		this.buildingSign = buildingSign;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "Building [buildingSign=" + buildingSign + ", dataType=" + dataType + "]";
	}

}
