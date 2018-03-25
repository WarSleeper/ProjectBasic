package com.gemo.thread;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseThread implements Runnable {

	private static final Map<String,BaseThread> threadMap = new HashMap<>();
	
	private String name;
	
	private Boolean stop = true;
	
	private Boolean realStop = true;
	
	public Boolean getStop() {
		return stop;
	}

	public void setStop(Boolean stop) {
		this.stop = stop;
	}

	public String getName() {
		if(name == null){
			name = this.toString();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getRealStop() {
		return realStop;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		stop = false;
		realStop = false;
		while(!stop){
			try {
				this.business();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				stop = true;
				e.printStackTrace();
			}
		}
		realStop = true;
	}

	public Boolean start(){
		if(stop && realStop){
			if(threadMap.get(getName())==null){
				threadMap.put(getName(), this);
			}
			Thread thread = new Thread(this,name);
			thread.start();
			return true;
		}
		return false;
	}
	
	public static BaseThread getBaseThread(String name){
		return threadMap.get(name);
	}
	
	public static void removeBaseThread(String name){
		threadMap.remove(name);
	}
	
	protected abstract void business() throws Exception;
}
