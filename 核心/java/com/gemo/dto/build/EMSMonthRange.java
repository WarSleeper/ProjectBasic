package com.gemo.dto.build;

import java.util.Date;
import java.util.List;

import com.gemo.enumeration.SpecialOperator;

public class EMSMonthRange {

	private List<Date> equalList;

	private SpecialOperator greaterOperator;

	private Date greaterDate;

	private SpecialOperator lessOperator;

	private Date lessDate;

	public SpecialOperator getGreaterOperator() {
		return greaterOperator;
	}

	public void setGreaterOperator(SpecialOperator greaterOperator) {
		this.greaterOperator = greaterOperator;
	}

	public Date getGreaterDate() {
		return greaterDate;
	}

	public void setGreaterDate(Date greaterDate) {
		this.greaterDate = greaterDate;
	}

	public SpecialOperator getLessOperator() {
		return lessOperator;
	}

	public void setLessOperator(SpecialOperator lessOperator) {
		this.lessOperator = lessOperator;
	}

	public Date getLessDate() {
		return lessDate;
	}

	public void setLessDate(Date lessDate) {
		this.lessDate = lessDate;
	}

	public List<Date> getEqualList() {
		return equalList;
	}

	public void setEqualList(List<Date> equalList) {
		this.equalList = equalList;
	}

}
