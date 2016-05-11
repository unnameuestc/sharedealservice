package com.bmi.sharedeal.service.Server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import com.bmi.sharedeal.service.Config;
import com.bmi.sharedeal.service.Handler.UserHandler;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 * @author xiaoyu
 * @data : 2015/8/14
 */

public class BmiServer {
	private int port = 80;
	private int threadCnt = 10;
	
	private HttpServer httpServer = null;
	
	public BmiServer (int port, int threadCnt) {
		this.port = port;
		this.threadCnt = threadCnt;
		
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), threadCnt);
		} catch (IOException e) {
			if (Config.DEBUG){
				e.printStackTrace();
			}
		}
	}
	
	public boolean addHandler (String path, BaseHandler handler) {
		if( httpServer == null){
			return false;
		}
		
		//A filter used to pre- and post-process incoming requests.
		ParameterFilter filter = new ParameterFilter();
		
		try {
			//getClass（）函数，从java.lang.Object类继承得到，该函数返回此Object的运行时类
			Class<?> c = handler.getClass();	
			Method methods[] = c.getMethods();
			for (Method m : methods) {
				//已 String 形式返回此Method对象表示的方法名称
				String name = m.getName();
				System.out.println("name的值：" + name);
				if (name.startsWith("api_")) {
					HttpContext context = httpServer.createContext(path + name.substring(4), handler);
					context.getFilters().add(filter);
				}
			}
			
		} catch (Exception e) {
			if (Config.DEBUG){
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	public boolean start() {
		if (httpServer == null) {
			return false;
		}
		
		httpServer.start();		
		System.out.println("Server Listen port " + port + "...");
		return true;
	}
}
