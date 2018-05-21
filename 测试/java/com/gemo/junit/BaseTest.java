package com.gemo.junit;

import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gemo.constant.SystemConstant;
import com.gemo.constant.SystemSetting;
import com.gemo.mvc.service.CoreService;
import com.gemo.thread.EmsMonthThread;

/**
 * 基础测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/spring/spring-base.xml" })
public abstract class BaseTest extends AbstractJUnit4SpringContextTests {

	private static Logger log = Logger.getLogger(BaseTest.class);
	@Resource
	protected SystemSetting systemSetting;
	 
	@Resource(name = "coreService")
	protected CoreService coreService;

	protected static final SimpleDateFormat standard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		PropertyConfigurator
				.configure(Thread.currentThread().getContextClassLoader().getResource("config/log4j/log4j.properties"));
	}

	@Before
	public void before() {

		SystemConstant.context = systemSetting.getContext();
		if (systemSetting.getJdbcDriverClass().equals(SystemConstant.JdbcDriverClass.MYSQL)) {
			EmsMonthThread emsMonthThread = SystemConstant.context.getBean(EmsMonthThread.class);

			try {
				emsMonthThread.refresh();
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}

	}

	@Test
	public void test() {
		log.info("******** 测试开始 ********");
		handle();
		log.info("******** 测试结束 ********");
	}

	protected abstract void handle();

}
