package com.gemo.dto.interpreter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.constant.EMSConstant.Structure;

public class Udm {

	// 数据标识
	@JsonProperty(Structure.MARK)
	private String mark;
	// 数据对象
	@JsonProperty(Structure.COLLECTION)
	private String collection;

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

	@Override
	public String toString() {
		return "Udm [collection=" + collection + ", mark=" + mark + "]";
	}

}
