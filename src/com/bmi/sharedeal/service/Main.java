package com.bmi.sharedeal.service;

import com.bmi.sharedeal.service.Handler.BuyHandler;
import com.bmi.sharedeal.service.Handler.DealHandler;
import com.bmi.sharedeal.service.Handler.DebtInfoHandler;
import com.bmi.sharedeal.service.Handler.EntrustInfoHandler;
import com.bmi.sharedeal.service.Handler.IndexHandler;
import com.bmi.sharedeal.service.Handler.OtherHandler;
import com.bmi.sharedeal.service.Handler.SellHandler;
import com.bmi.sharedeal.service.Handler.UserHandler;
import com.bmi.sharedeal.service.Server.BmiServer;

/**
 * 
 * @author xiaoyu
 *
 */

public class Main {
	
//	private ApiMonitor monitor = ApiMonitor.get();
	
	public static void main(String[] args) {
		
		//初始化配置
		if(!Config.init()) {
			System.out.println("Error init configuration!");
			return;
		}
		
		BmiServer server = new BmiServer(Config.PortNum, Config.ThreadCnt);	
		
		//首页
		server.addHandler("/", new IndexHandler());
		//用户
		server.addHandler("/user/", new UserHandler());
		//交易模块
		server.addHandler("/buy/", new BuyHandler());
		server.addHandler("/sell/", new SellHandler());
		server.addHandler("/entrust/", new EntrustInfoHandler());
		server.addHandler("/deal/", new DealHandler());
		server.addHandler("/debt/", new DebtInfoHandler());
		
		//其他
		server.addHandler("/other/", new OtherHandler());
		server.start();
	}

}
