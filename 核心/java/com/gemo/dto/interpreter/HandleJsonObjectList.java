package com.gemo.dto.interpreter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HandleJsonObjectList {

	@JsonProperty("list")
	private List<HandleJsonObject> list;

	public List<HandleJsonObject> getList() {
		return list;
	}

	public void setList(List<HandleJsonObject> list) {
		this.list = list;
	}
	
}
