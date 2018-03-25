package com.gemo.mvc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.annotation.Column;

public abstract class BaseProperty extends BaseId {

	private static final long serialVersionUID = -7252145194936617141L;

	@Column(order = Integer.MAX_VALUE - 1, name = "c_property_name", length = 40, nullable = false, comment = "属性名")
	@JsonProperty("propertyName")
	private String propertyName;

	@Column(order = Integer.MAX_VALUE, name = "c_property_value", length = 500, nullable = true, comment = "属性值")
	@JsonProperty("propertyValue")
	private String propertyValue;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

}
