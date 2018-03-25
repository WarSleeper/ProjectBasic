package com.gemo.mvc.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gemo.enumeration.EMSOrder;
import com.gemo.mvc.dao.CoreDao;
import com.gemo.mvc.pojo.BusinessObject;
import com.gemo.mvc.service.CoreService;

/**
 * 以JdbcTemplate形式的基础业务处理层实现
 */
@Service("coreService")
@Scope("prototype")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CoreServiceImpl implements CoreService {

	@Resource(name = "jdbcTemplateCoreDao")
	protected CoreDao coreDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public int executeUpdate(String sql, List<Object> paramList) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.executeUpdate(sql, paramList);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> List<T> query(String sql, List<Object> args, Class<T> cls) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.query(sql, args, cls);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> List<T> query(T object) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.query(object);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
	@Override
	public List<Map<String, Object>> queryBySql(String sql, List<Object> args) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.queryBySql(sql, args);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> int remove(T object) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.remove(object);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> int save(T object) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.save(object);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> int update(T objectCriteria, T objectUpdate) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.update(objectCriteria, objectUpdate);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
	@Override
	public int count(String sql, List<Object> args) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.count(sql, args);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> int count(T object) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.count(object);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> int save(List<T> list) throws Exception {
		// TODO Auto-generated method stub
		int count = 0;
		if (list != null) {
			for (T object : list) {
				count = count + this.save(object);
			}
		}

		return count;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> int remove(List<T> list) throws Exception {
		// TODO Auto-generated method stub
		int count = 0;
		if (list != null) {
			for (T object : list) {
				count = count + this.remove(object);
			}
		}
		return count;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> List<T> query(T object, Boolean isCascade) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.query(object, isCascade);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public <T extends BusinessObject> int remove(T object, Boolean isCascade) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.remove(object, isCascade);
	}

	@Override
	public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.batchUpdate(sql, batchArgs);
	}

	@Override
	public <T extends BusinessObject> List<T> batchQuery(List<T> objectList, LinkedHashMap<String, EMSOrder> sortMap,
			Long skip, Long limit) throws Exception {
		// TODO Auto-generated method stub
		return coreDao.batchQuery(objectList, sortMap, skip, limit);
	}

}
