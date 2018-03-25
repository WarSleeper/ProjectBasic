package com.gemo.enumeration;

/**
 * 支持特殊操作类型
 */
public enum SpecialOperator {

	$lt(" < "), $lte(" <= "), $gt(" > "), $gte(" >= "), $ne(" <> "), $like(" like "), $in(" in "), $nin(
			" not in "), $exists(" is "), @Deprecated
	$null(" is null "), @Deprecated
	$not_null(" is not null ");

	private String operator;

	SpecialOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}
}
