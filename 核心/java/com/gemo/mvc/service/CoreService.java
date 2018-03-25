package com.gemo.mvc.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gemo.enumeration.EMSOrder;
import com.gemo.mvc.pojo.BusinessObject;

/**
 * 基础业务处理层
 */
public interface CoreService {

	/**
	 * PreparedStatement形式执行insert、update、delete、create形式sql
	 * 
	 * @param sql
	 *            条件值为占位符(?)形式的sql
	 * @param paramList
	 *            与占位符顺序一致的值列表
	 * @return sql影响行数
	 * @throws Exception
	 */
	public int executeUpdate(String sql, List<Object> paramList) throws Exception;

	/**
	 * PreparedStatement形式执行select形式sql
	 * 
	 * @param sql
	 *            条件值为占位符(?)形式的sql
	 * @param args
	 *            与占位符顺序一致的值列表
	 * @return 返回以Map对象为元素的List集合
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryBySql(String sql, List<Object> args) throws Exception;

	/**
	 * PreparedStatement形式执行select形式sql
	 * 
	 * @param <T>
	 *            继承自BusinessObject的泛型
	 * @param sql
	 *            条件值为占位符(?)形式的sql
	 * @param args
	 *            与占位符顺序一致的值列表
	 * @param cls
	 *            继承自BusinessObject的泛型对象的字节码
	 * @return 返回以泛型对象为元素的List集合
	 * @throws Exception
	 */
	public <T extends BusinessObject> List<T> query(String sql, List<Object> args, Class<T> cls) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式select sql查询
	 * 
	 * @param <T>
	 *            继承自BusinessObject的泛型
	 * @param object
	 *            继承自BusinessObject的泛型对象
	 * @return 返回以泛型对象为元素的List集合
	 * @throws Exception
	 */
	public <T extends BusinessObject> List<T> query(T object) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式select sql查询
	 * 
	 * @param object
	 *            继承自BusinessObject的泛型
	 * @param isCascade
	 *            是否级联查询
	 * @return 返回以泛型对象为元素的List集合
	 * @throws Exception
	 */
	public <T extends BusinessObject> List<T> query(T object, Boolean isCascade) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式insert sql添加
	 * 
	 * @param <T>
	 *            继承自BusinessObject的泛型
	 * @param object
	 *            继承自BusinessObject的泛型对象
	 * @return 返回影响记录数
	 * @throws Exception
	 */
	public <T extends BusinessObject> int save(T object) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式delete sql删除
	 * 
	 * @param <T>
	 *            继承自BusinessObject的泛型
	 * @param object
	 *            继承自BusinessObject的泛型对象
	 * @return 返回影响记录数
	 * @throws Exception
	 */
	public <T extends BusinessObject> int remove(T object) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式delete sql删除
	 * 
	 * @param object
	 *            继承自BusinessObject的泛型
	 * @param isCascade
	 *            是否级联删除
	 * @return 返回影响记录数
	 * @throws Exception
	 */
	public <T extends BusinessObject> int remove(T object, Boolean isCascade) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式update sql更新
	 * 
	 * @param <T>
	 *            继承自BusinessObject的泛型
	 * @param objectCriteria
	 *            继承自BusinessObject的泛型对象用于构建where
	 * @param objectUpdate
	 *            继承自BusinessObject的泛型对象用于构建set
	 * @return 返回影响记录数
	 * @throws Exception
	 */
	public <T extends BusinessObject> int update(T objectCriteria, T objectUpdate) throws Exception;

	/**
	 * PreparedStatement形式执行select count(*)形式sql
	 * 
	 * @param sql
	 *            条件值为占位符(?)形式的sql
	 * @param args
	 *            与占位符顺序一致的值列表
	 * @return count记录数
	 * @throws Exception
	 */
	public int count(String sql, List<Object> args) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式select count查询
	 * 
	 * @param <T>
	 *            继承自BusinessObject的泛型
	 * @param object
	 *            继承自BusinessObject的泛型对象
	 * @return count记录数
	 * @throws Exception
	 */
	public <T extends BusinessObject> int count(T object) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式insert sql 批量添加
	 * 
	 * @param <T>
	 *            继承自BusinessObject的泛型
	 * @param list
	 *            继承自BusinessObject的List泛型对象
	 * @return 返回影响记录数
	 * @throws Exception
	 */
	public <T extends BusinessObject> int save(List<T> list) throws Exception;

	/**
	 * 以对象形式解析为PreparedStatement形式delete sql批量删除
	 * 
	 * @param <T>
	 *            继承自BusinessObject的泛型
	 * @param list
	 *            继承自BusinessObject的List泛型对象
	 * @return 返回影响记录数
	 * @throws Exception
	 */
	public <T extends BusinessObject> int remove(List<T> list) throws Exception;

	/**
	 * 
	 * @param sql
	 *            批量执行的sql 语句
	 * @param batchArgs
	 *            被执行的sql 参数列表
	 * @return 执行结果数组
	 * @throws Exception
	 */
	public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws Exception;

	/**
	 * 
	 * @param objectList
	 * @return
	 * @throws Exception
	 */
	public <T extends BusinessObject> List<T> batchQuery(List<T> objectList, LinkedHashMap<String, EMSOrder> sortMap,
			Long skip, Long limit) throws Exception;
}
