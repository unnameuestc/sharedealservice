package com.bmi.sharedeal.service.Handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;

import com.bmi.sharedeal.service.DAO.Buy;
import com.bmi.sharedeal.service.DAO.DaoConst;
import com.bmi.sharedeal.service.DAO.Sell;
import com.bmi.sharedeal.service.DAO.User;
import com.bmi.sharedeal.service.Server.BaseHandler;
import com.bmi.sharedeal.service.utils.TextUtils;

public class SellHandler extends BaseHandler{
	//判断是否登录
	
	//得到登录用户id
	
	//得到表单数据
	
	//更新t_sell表和t_user表
	
	/**
	 * 卖出股票
	 */
	public void api_sellShares() {
		//判断是否登录
		User user = UserHandler.checkUserAuth(this);
		if(user == null) {
			return;
		}
		
		//填写卖出信息
		String _price = (String) this.getArgument("price");
		String _shareNum = (String) this.getArgument("sharenum");
		
		if( TextUtils.isEmpty(_shareNum) || TextUtils.isEmpty(_price)) {
			this.writeError("参数不足");
			return;
		}
		
		float price = Float.parseFloat(_price);
		int shareNum = Integer.parseInt(_shareNum);
				
		if(price <= 0 || shareNum <= 0) {
			return;
		}
		
		int frozenNum = user.getFrozenNum();
		int tradableNum = user.getTradableNum();
		if(shareNum > tradableNum) {
			return;
		}
		
		Sell sell = new Sell();
		sell.setUserId(user.getId());
		sell.setTime(new Date());
		sell.setBuyOrSell(DaoConst.BUYORSELL_SELL);
		sell.setPrice(price);
		sell.setShareNum(shareNum);
		sell.setFinishedShareNum(0);
		sell.setState(DaoConst.STATE_INITIAL);
		
		user.setTradableNum(tradableNum - shareNum);
		user.setFrozenNum(frozenNum + shareNum);
		
		//更新t_sell表和t_user表
		if(dao.insert(sell) == null || dao.update(user) == 0) {
			this.writeError("数据库操作错误");
            return;
		}
		
		//撮合交易
		DealHandler deal = new DealHandler();
		System.out.println("***********************************************sell.getId():" + sell.getId());
		deal.doSell(sell.getId());
				
		//得到撮合交易之后的委托信息
		sell = dao.fetch(Sell.class, Cnd.where("id", "=", sell.getId()));		
		Map res = new HashMap();
		res.put("sell", sell);
		this.writeResult(res);
	}
	
	/**
	 * 查询最近5笔委托卖出信息
	 */
	
	public void api_getSellInfo() {
		
//		Sql sql = Sqls.queryEntity("SELECT * FROM t_sell where state < " + DaoConst.STATE_FINISHIN + " ORDER BY id DESC LIMIT " + DaoConst.DEALNUM_SELL + "");
//		sql.setCondition(Cnd.orderBy().desc("price"));
		Sql sql = Sqls.queryEntity("SELECT price, SUM(sharenum) AS sharenum FROM t_sell WHERE state < " 
				+ DaoConst.STATE_FINISHIN + " GROUP BY price LIMIT " + DaoConst.DEALNUM_BUY + "");
		sql.setEntity(dao.getEntity(Sell.class));
		dao.execute(sql);
		List<Sell> sells = sql.getList(Sell.class);
		
		Map res = new HashMap();
		res.put("sells", sells);
		
		this.writeResult(res);
	}
}
