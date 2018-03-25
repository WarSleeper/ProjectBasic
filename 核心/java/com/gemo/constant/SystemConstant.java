package com.gemo.constant;

import java.text.SimpleDateFormat;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import freemarker.template.Configuration;

/**
 * 系统常量类
 */
public class SystemConstant {

	public static ApplicationContext context;
	public static final String REPLACE_TABLE = "replace_table";
	public static final String REPLACE_INDEX = "replace_index";

	public static final ObjectMapper jsonMapper = new ObjectMapper();
	public static final ObjectMapper xmlMapper = new XmlMapper();
	static {
		jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	public static final Configuration freemarker = new Configuration(Configuration.VERSION_2_3_21);
	static {
		freemarker.setDefaultEncoding("UTF-8");
		freemarker.setWhitespaceStripping(true);
		freemarker.setNumberFormat("#");
		try {
			freemarker.setClassForTemplateLoading(SystemConstant.class, "/config/freemarker");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class JdbcDriverClass {
		// oracle jdbc
		public static final String ORALCE = "oracle.jdbc.driver.OracleDriver";
		// mysql jdbc
		public static final String MYSQL = "com.mysql.jdbc.Driver";
		// sqlserver jdbc
		public static final String SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		// sqlite jdbc
		public static final String SQLITE = "org.sqlite.JDBC";
		// mysql jdbc
		public static final String ESGYN = "org.trafodion.jdbc.t4.T4Driver";

	}
}
