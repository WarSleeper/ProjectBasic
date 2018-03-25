package com.gemo.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.gemo.annotation.Month;
import com.gemo.annotation.Table;
import com.gemo.constant.SchemaConstant;
import com.gemo.constant.SystemConstant.JdbcDriverClass;
import com.gemo.constant.SystemSetting;
import com.gemo.dto.analysis.EMSAnalysis;
import com.gemo.mvc.service.CoreService;
import com.gemo.utils.CommonUtils;

/**
 * 线程－刷新月份分表集合
 */
@Component("emsMonthThread")
public class EmsMonthThread implements Runnable {

	private static Logger log = Logger.getLogger(EmsMonthThread.class);
	// 分表集合
	private static final Map<String, List<String>> tableMap = new ConcurrentHashMap<String, List<String>>();

	@Resource
	private SystemSetting systemSetting;
	@Resource(name = "coreService")
	private CoreService coreService;

	private Boolean isRepairFinished;

	private Boolean isRefresh;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// 只有MySql分表
		if (systemSetting.getJdbcDriverClass().equals(JdbcDriverClass.MYSQL)) {
			isRefresh = false;
			if (systemSetting.getSystemRepairTable()) {
				isRepairFinished = false;
				try {
					this.repairTable();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.info("MySql 检查表失败 ......");
					e.printStackTrace();
				}
			}
			isRepairFinished = true;
			while (true) {
				try {
					log.info(" ******* reset master 开始 ****** ");
					resetMaster();
					log.info(" ******* reset master 结束 ****** ");
					log.info(" ******* 分表刷新 开始 ****** ");
					refresh();
					log.info(" ******* 分表刷新 成功，10分钟后继续 ****** ");
					CommonUtils.sleep(DateUtils.MILLIS_PER_MINUTE * 10);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.info(" ******* 分表刷新 失败，1分钟后继续 ****** ");
					e.printStackTrace();
					CommonUtils.sleep(DateUtils.MILLIS_PER_MINUTE);
				}
			}
		} else {
			isRepairFinished = true;
			isRefresh = true;
		}
	}

	/**
	 * 获取分表List集合
	 * 
	 * @param tableNameKey
	 *            无yyyyMM_前缀的表名
	 * @return
	 */
	public static synchronized List<String> getTableList(String tableNameKey) {
		List<String> temp = new ArrayList<String>();
		if (tableMap.get(tableNameKey) != null) {
			temp.addAll(tableMap.get(tableNameKey));
			Collections.sort(temp);
		}
		return temp;
	}

	/**
	 * 添加分表
	 * 
	 * @param tableNameKey
	 *            无yyyyMM_前缀的表名
	 * @param tableName
	 *            分表名称
	 */
	public static synchronized void setTableList(String tableNameKey, String tableName) {
		List<String> tableList = tableMap.get(tableNameKey);
		if (tableList == null) {
			tableList = new CopyOnWriteArrayList<String>();
			tableMap.put(tableNameKey, tableList);
		}
		if (!tableList.contains(tableName)) {
			tableList.add(tableName);
		}

	}

	/**
	 * 查询刷新分表
	 * 
	 * @throws Exception
	 */
	public void refresh() throws Exception {

		for (Class<?> cls : EMSAnalysis.getClassSet()) {

			if (cls.isAnnotationPresent(Month.class)) {

				Table table = (Table) cls.getAnnotation(Table.class);
				String tableName = table.name();
				String schemaKey = table.schema().getValue();
				String schema = SchemaConstant.getSchema(schemaKey);
				StringBuffer sql = new StringBuffer(" select table_name from information_schema.tables ");
				sql.append(" where table_schema = '" + schema + "' and table_name like '______\\_" + tableName + "%' ");
				sql.append(" order by table_name ");
				List<Map<String, Object>> result = coreService.queryBySql(sql.toString(), null);

				for (Map<String, Object> map : result) {
					String splitTableName = (String) map.get("table_name");
					String tableKey = splitTableName.substring(7);
					setTableList(tableKey, splitTableName);
				}
			}
		}

		isRefresh = true;
	}

	public void resetMaster() {
		try {
			coreService.executeUpdate("reset master", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}

	public Boolean getIsRepairFinished() {
		return new Boolean(isRepairFinished);
	}

	private void repairTable() throws Exception {
		log.info("MySql 检查表开始 ......");

		List<String> schemaList = new ArrayList<String>();

		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.WORKFLOW.getValue()));
		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.ORIGINALDATA.getValue()));
		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.METERDATA.getValue()));
		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.SERVICEDATA.getValue()));
		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.OBJECTDATA.getValue()));
		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.ENERGYDATA.getValue()));
		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.TENEMENTDATA.getValue()));
		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.EMSVDATA.getValue()));
		schemaList.add(SchemaConstant.getSchema(SchemaConstant.Schema.EMS.getValue()));

		String sql = " select table_name from information_schema.tables where table_schema = ? ";
		List<Object> paramList = new ArrayList<Object>();
		for (String schema : schemaList) {
			paramList.clear();
			paramList.add(schema);
			List<Map<String, Object>> result = coreService.queryBySql(sql, paramList);
			for (Map<String, Object> map : result) {
				String tableName = schema + "." + (String) map.get("table_name");
				String repairSql = "REPAIR TABLE " + tableName;
				log.info(" Repair " + tableName + " ... ");
				coreService.executeUpdate(repairSql, null);
			}
		}

		log.info("MySql 检查表结束 ......");
	}

	public Boolean getIsRefresh() {
		if (isRefresh == null) {
			isRefresh = false;
		}
		return isRefresh;
	}
}
