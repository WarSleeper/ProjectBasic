package com.gemo.junit.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.gemo.constant.SystemConstant;
import com.gemo.junit.BaseTest;
import com.gemo.mvc.pojo.BusinessObject;

/**
 * 基础测试类
 */
public class EMSTest extends BaseTest {

	private static Logger log = Logger.getLogger(EMSTest.class);

	@Override
	protected void handle() {
		// TODO Auto-generated method stub

	}

	private <T extends BusinessObject> void insert(T object) {
		try {
			int count = coreService.save(object);
			log.info(" insert rows " + count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private <T extends BusinessObject> void insert(List<T> list) {
		long start = System.currentTimeMillis();
		try {
			int count = coreService.save(list);
			log.info(" insert rows " + count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("操作用时：" + (end - start) + "毫秒/" + ((end - start) / 1000.0) + "秒");
	}

	private <T extends BusinessObject> void query(T object) {
		long start = 0l;
		long end = 0l;
		try {
			start = System.currentTimeMillis();
			List<T> result = coreService.query(object);
			end = System.currentTimeMillis();
			int idx = 0;
			for (T o : result) {
				log.info(idx + " - " + SystemConstant.jsonMapper.writeValueAsString(o));
				idx++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("操作用时：" + (end - start) + "毫秒/" + ((end - start) / 1000.0) + "秒");
	}

	private <T extends BusinessObject> void queryCascade(T object) {
		long start = 0l;
		long end = 0l;
		try {
			start = System.currentTimeMillis();
			List<T> result = coreService.query(object, true);
			end = System.currentTimeMillis();
			int idx = 0;
			for (T o : result) {
				log.info(idx + " - " + SystemConstant.jsonMapper.writeValueAsString(o));
				idx++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("操作用时：" + (end - start) + "毫秒/" + ((end - start) / 1000.0) + "秒");
	}

	private <T extends BusinessObject> void remove(T object) {
		try {
			int count = coreService.remove(object);
			log.info(" delete rows " + count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private <T extends BusinessObject> void removeCascade(T object) {
		try {
			int count = coreService.remove(object, true);
			log.info(" delete rows " + count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private <T extends BusinessObject> void update(T objectCriteria, T objectUpdate) {
		try {
			int count = coreService.update(objectCriteria, objectUpdate);
			log.info(" update rows " + count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private <T extends BusinessObject> void count(T object) {
		try {
			int count = coreService.count(object);
			log.info(" count rows " + count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
