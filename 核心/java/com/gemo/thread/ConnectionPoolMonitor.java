package com.gemo.thread;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.PooledDataSource;

@Component("connectionPoolMonitor")
public class ConnectionPoolMonitor implements Runnable {

	private static Logger log = Logger.getLogger(ConnectionPoolMonitor.class);
	
	@Resource(name = "dataSource")
	private PooledDataSource dataSource;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			log.info("***** 连接池监控 *****");
			try {
				monitor();
				Thread.sleep(1000 * 60L);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void monitor() throws Exception {
		//空闲连接
		int numIdleConnections =  dataSource.getNumIdleConnectionsDefaultUser();
		//使用连接
		int numBusyConnections = dataSource.getNumBusyConnectionsDefaultUser();
		//总连接数
		int numConnections = dataSource.getNumConnectionsDefaultUser();
		//泄露连接
		int numUnclosedOrphanedConnections = dataSource.getNumUnclosedOrphanedConnectionsDefaultUser();
		
		log.info("  总连接数：" + numConnections);
		log.info("空闲连接数：" + numIdleConnections);
		log.info("在用连接数：" + numBusyConnections);
		log.info("泄露连接数：" + numUnclosedOrphanedConnections);
	}
	
}
