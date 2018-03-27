package com.gemo.web.controller.entrance.dto;

public class FileConfigDTO {
	
	private String fileName;
	
	private String fileSuffix;
	
	private String subdirectory;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	public String getSubdirectory() {
		return subdirectory;
	}

	public void setSubdirectory(String subdirectory) {
		this.subdirectory = subdirectory;
	}

	@Override
	public String toString() {
		return "FileConfigDTO [fileName=" + fileName + ", fileSuffix="
				+ fileSuffix + ", subdirectory=" + subdirectory + "]";
	}
	
}
