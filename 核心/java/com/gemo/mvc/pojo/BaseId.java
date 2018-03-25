package com.gemo.mvc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.annotation.Column;
import com.gemo.annotation.Id;

public abstract class BaseId extends BusinessObject {

	private static final long serialVersionUID = 8629633598807988185L;

	@Id
	@Column(order = Integer.MIN_VALUE, name = "c_id", length = 36, nullable = false, comment = "主键")
	@JsonProperty("id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
