package com.gemo.dto.interpreter;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.constant.EMSConstant.Structure;

public class Limit {

	// 限制记录数
	@JsonProperty(Structure.LIMIT)
	private Long limit;
	// 跳过记录数
	@JsonProperty(Structure.SKIP)
	private Long skip;
	// 排序
	@JsonProperty(Structure.SORT)
	private List<Map<String, Integer>> sort;

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public Long getSkip() {
		return skip;
	}

	public void setSkip(Long skip) {
		this.skip = skip;
	}

	public List<Map<String, Integer>> getSort() {
		return sort;
	}

	public void setSort(List<Map<String, Integer>> sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "Limit [limit=" + limit + ", skip=" + skip + ", sort=" + sort + "]";
	}

}
