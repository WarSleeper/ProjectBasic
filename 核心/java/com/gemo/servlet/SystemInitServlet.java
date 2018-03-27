package com.gemo.servlet;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gemo.constant.SystemConstant;
import com.gemo.service.SystemInitService;

public class SystemInitServlet extends HttpServlet {

	private static final long serialVersionUID = 920939442057846697L;

	private static final Logger log = Logger.getLogger(SystemInitServlet.class);

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		log.info("系统初始化开始......");
		ServletContext sc = this.getServletContext();
		SystemConstant.context = WebApplicationContextUtils.getWebApplicationContext(sc);

		this.createBusinessThread("emsMonthThread");

		Map<Integer, SystemInitService> map = new TreeMap<Integer, SystemInitService>();
		for (Map.Entry<String, SystemInitService> entry : SystemConstant.context.getBeansOfType(SystemInitService.class)
				.entrySet()) {
			map.put(entry.getValue().order(), entry.getValue());
		}
		for (Map.Entry<Integer, SystemInitService> entry : map.entrySet()) {
			try {
				entry.getValue().init();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
		}

		log.info("系统初始化结束......");
	}

	private void createBusinessThread(String name) {
		Runnable runnable = (Runnable) SystemConstant.context.getBean(name);
		Thread thread = new Thread(runnable);
		thread.setName(name);
		thread.start();
	}
}
