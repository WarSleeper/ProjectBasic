package com.gemo.mvc.pojo;

import com.gemo.enumeration.SpecialOperator;

/**
 * 特殊操作类
 */
public class SpecialOperation {

	// 操作符
	private SpecialOperator specialOperator;
	// 值
	private Object value;

	public SpecialOperator getSpecialOperator() {
		return specialOperator;
	}

	public void setSpecialOperator(SpecialOperator specialOperator) {
		this.specialOperator = specialOperator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SpecialOperation [specialOperator=" + specialOperator + ", value=" + value + "]";
	}

}
