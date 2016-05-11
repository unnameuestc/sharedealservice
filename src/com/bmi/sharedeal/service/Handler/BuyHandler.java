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
import com.bmi.sharedeal.service.DAO.User;
import com.bmi.sharedeal.service.Server.BaseHandler;
import com.bmi.sharedeal.service.Handler.DealHandler;
import com.bmi.sharedeal.service.utils.TextUtils;

public class BuyHandler extends BaseHandler{
	
	/**
	 * 买入股票：先将委托信息插入t_buy然后进行撮合并更新t_buy中的state字段
	 */
	public void api_buyShares() {
		//判断是否登录
		User user = UserHandler.checkUserAuth(this);
		if(user == null) {
			return;
		}
		
		//填写购买信息
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
		
		float frozenFund = user.getFrozenFund();
		float fund = user.getUsableFund();
		int maxShareNum = (int) (fund / price);
		
		if(shareNum > maxShareNum) {
			return;
		}
		
		//委托词条插入到t_buy表中
		Buy buy = new Buy();
		buy.setUserId(user.getId());
		buy.setTime(new Date());
		buy.setBuyOrSell(DaoConst.BUYORSELL_BUY);
		buy.setPrice(price);
		buy.setShareNum(shareNum);
		buy.setFinishedShareNum(0);
		buy.setState(DaoConst.STATE_INITIAL);
		
		user.setUsableFund(fund - shareNum * price);
		user.setFrozenFund(frozenFund + shareNum * price);
		
		//更新t_sell表和t_user表
		if(dao.insert(buy) == null || dao.update(user) <= 0) {
			this.writeError("数据库操作错误");
            return;
		}
		
		//撮合交易
		DealHandler deal = new DealHandler();
//		System.out.println("buyId:" + buy.getId());
		deal.doBuy(buy.getId());
		
		//得到撮合交易之后的委托信息
		buy = dao.fetch(Buy.class, Cnd.where("id", "=", buy.getId()));
		Map res = new HashMap();
		res.put("buy", buy);
		this.writeResult(res);
	}
	
	/**
	 * 查询最近5笔委托买入信息
	 */
	
	public void api_getBuyInfo() {
//		Sql sql = Sqls.queryEntity("SELECT * FROM t_buy ORDER BY id DESC LIMIT " + DaoConst.DEALNUM_BUY + "$condition");
//		Sql sql = Sqls.queryEntity("SELECT * FROM t_buy where state < " + DaoConst.STATE_FINISHIN + " ORDER BY id DESC LIMIT " + DaoConst.DEALNUM_BUY + "$condition");
//		sql.setEntity(dao.getEntity(Buy.class));
//		dao.execute(sql);
//		List<Buy> buys = sql.getList(Buy.class);
		
		Sql sql = Sqls.queryEntity("SELECT price, SUM(sharenum) AS sharenum FROM t_buy WHERE state <" 
					+ DaoConst.STATE_FINISHIN + " GROUP BY price LIMIT " + DaoConst.DEALNUM_BUY + "$condition");
		sql.setEntity(dao.getEntity(Buy.class));
		dao.execute(sql);
		List<Buy> buys = sql.getList(Buy.class);
		
		Map res = new HashMap();
		res.put("buys", buys);
		
		this.writeResult(res);
	}
}
