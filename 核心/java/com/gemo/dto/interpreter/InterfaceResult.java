package com.gemo.dto.interpreter;

import java.util.List;

/**
 * 对外接口DTO
 * 
 * @version v1.0
 */
@SuppressWarnings("rawtypes")
public class InterfaceResult {

	private String version;

	private String result;

	private String reason;

	private List content;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public List getContent() {
		return content;
	}

	public void setContent(List content) {
		this.content = content;
	}

}
