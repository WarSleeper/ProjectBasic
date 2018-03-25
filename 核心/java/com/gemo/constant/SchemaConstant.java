package com.gemo.constant;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 对象区分所在数据库的常量配置
 */
@SuppressWarnings("unchecked")
public class SchemaConstant {

	public static enum Schema {
		EMS, UDM, EMSVDATA, TENANT, WORKFLOW, ENERGYDATA, OBJECTDATA, METERDATA, ORIGINALDATA, SERVICEDATA, TENEMENTDATA, NONE;

		public String getValue() {
			return this.name().toLowerCase();
		}
	}

	// 对象－数据库配置
	private static final Map<String, String> schemaMap = new HashMap<String, String>();

	public static String getSchema(String key) {
		return schemaMap.get(key);
	}

	public static Map<String, String> getSchemaMap() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.putAll(schemaMap);
		return tempMap;
	}

	static {
		URL url = SchemaConstant.class.getResource("/config/schema.json");
		try {
			Map<String, String> temp = SystemConstant.jsonMapper.readValue(url, Map.class);
			schemaMap.putAll(temp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
