package com.gemo.constant;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 系统属性文件常量类
 */
@Component
public class SystemSetting {

	@Resource
	private ApplicationContext context;

	@Value("${jdbc.driverClass}")
	private String jdbcDriverClass;

	@Value("${jdbc.jdbcUrl}")
	private String jdbcUrl;

	@Value("${system.printSql}")
	private String systemPrintSql;

	@Value("${system.repairTable}")
	private String systemRepairTable;

	public String getJdbcDriverClass() {
		return jdbcDriverClass;
	}

	public Boolean getSystemPrintSql() {
		return new Boolean(systemPrintSql);
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public Boolean getSystemRepairTable() {
		return new Boolean(systemRepairTable);
	}

	public ApplicationContext getContext() {
		return context;
	}

}
