package com.bmi.sharedeal.service.Handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;

import com.bmi.sharedeal.service.DAO.*;
import com.bmi.sharedeal.service.Server.BaseHandler;

/**
 * 实现交易功能api_doBuy、api_doSell
 * 获取平台成交信息 api_getDealInfo
 * @author xiaoyu
 *
 */
public class DealHandler extends BaseHandler {
	/**
	 * 当用户点击买入时调用此函数实现买入功能
	 * @param buyId
	 */
	public void doBuy(int buyId) {
		//读取委托卖出表t_sell中的信息到链表队列中
		List<Sell> sells = dao.query(Sell.class, Cnd.where("state", "<", DaoConst.STATE_FINISHIN).asc("price"));
		//如果t_sells表为空，则返回
		if (sells.size() == 0) {
			return;
		}
				
		//比较此次委托买入价格和委托卖出表中的价格，若成交则更新t_buy和t_sell以及t_user表中的信息
		Buy buy = dao.fetch(Buy.class, Cnd.where("id", "=", buyId));
		float buyPrice = buy.getPrice();
		int buyNum = buy.getShareNum() - buy.getFinishedShareNum();
		int buyerId = buy.getUserId();
		
		int i = 0;
		Sell sell = sells.get(i);		
		int finishedShareNum = sell.getFinishedShareNum();
		int sellNum = sell.getShareNum() - finishedShareNum;
		float sellPrice = sell.getPrice();
		int sellId = sell.getId();	
		int sellerId = sell.getUserId();

		float dealPrice;
		//委托买入价格大于等于委托卖出价格的值，则成交		
		while (buyNum > 0 && buyPrice >= sellPrice) {				 				
			//更新t_buy,t_sell,t_user,t_deal
			dealPrice = (buyPrice + sellPrice)/2;
			if (buyNum <= sellNum) {
				//t_buy(更新)--成交股票数为buyNum
				buy.setFinishedShareNum(buy.getFinishedShareNum() + buyNum);
				buy.setState(DaoConst.STATE_FINISHIN);
				
				//t_sell(更新)
				sell.setFinishedShareNum(finishedShareNum + buyNum);
				if ( buyNum == sellNum) {
					sell.setState(DaoConst.STATE_FINISHIN);
				} else {
					sell.setState(DaoConst.STATE_PARTIAL);
				}
									
				//t_user(更新)
				User buyer = dao.fetch(User.class, Cnd.where("id", "=", buyerId));
				buyer.setTradableNum(buyer.getTradableNum() + buyNum);
				//注意实际成交价格和委托价格之差对冻结资金的影响
				buyer.setFrozenFund(buyer.getFrozenFund() - buyNum * buyPrice);				
				buyer.setUsableFund(buyer.getUsableFund() + buyNum * (buyPrice - dealPrice));
				User seller = dao.fetch(User.class, Cnd.where("id", "=", sellerId));
				seller.setFrozenNum(seller.getFrozenNum() - buyNum);
				seller.setUsableFund(seller.getUsableFund() + buyNum * dealPrice);
				
				//t_deal(插入)
				Deal deal = new Deal();
				deal.setBuyId(buyId);
				deal.setSellId(sellId);
				deal.setBuyerId(buyerId);
				deal.setSellerId(sellerId);
				deal.setTime(new Date());
				deal.setPrice(dealPrice);
				deal.setShareNum(buyNum);
				
				//操作数据库
				if (dao.update(buy) <= 0 || dao.update(sell) <= 0 || dao.update(buyer) <= 0 
						|| dao.update(seller) <= 0 || dao.insert(deal) == null) {
					this.writeError("数据库操作错误");
		            return;
				}
				
				//更新buyNum值
				buyNum = buyNum - sellNum;
				
			} else if (buyNum > sellNum) {
				//匹配t_sell表中下一个词条
				
				//t_buy
				buy.setFinishedShareNum(buy.getFinishedShareNum() + sellNum);
				buy.setState(DaoConst.STATE_PARTIAL);
				
				//t_sell
				sell.setFinishedShareNum(finishedShareNum + sellNum);
				sell.setState(DaoConst.STATE_FINISHIN);
				
				//t_user
				User buyer = dao.fetch(User.class, Cnd.where("id", "=", buyerId));
				buyer.setTradableNum(buyer.getTradableNum() + sellNum);
//				System.out.println("******************************************************");
//				System.out.println("buyer frozenFund:" + buyer.getFrozenFund());
//				System.out.println("******************************************************");
				buyer.setFrozenFund(buyer.getFrozenFund() - sellNum * buyPrice);
				buyer.setUsableFund(buyer.getUsableFund() + sellNum * (buyPrice - dealPrice));
				User seller = dao.fetch(User.class, Cnd.where("id", "=", sellerId));
				seller.setFrozenNum(seller.getFrozenNum() - sellNum);
				seller.setUsableFund(seller.getUsableFund() + sellNum * dealPrice);
				
				//t_deal
				Deal deal = new Deal();
				deal.setBuyId(buyId);
				deal.setSellId(sellId);
				deal.setBuyerId(buyerId);
				deal.setSellerId(sellerId);
				deal.setTime(new Date());
				deal.setPrice(dealPrice);
				deal.setShareNum(sellNum);
				
				//操作数据库
				if (dao.update(buy) <= 0 || dao.update(sell) <= 0 || dao.update(buyer) <= 0 
						|| dao.update(seller) <= 0 || dao.insert(deal) == null) {
					this.writeError("数据库操作错误");
		            return;
				}
				
				//更新buyNum值
				buyNum = buyNum - sellNum;	
				
				//匹配t_sell表中下一个词条
				i = i + 1;	
				sell = sells.get(i);
				finishedShareNum = sell.getFinishedShareNum();
				sellNum = sell.getShareNum() - finishedShareNum;
				sellPrice = sell.getPrice();
				sellId = sell.getId();
				sellerId = sell.getUserId();
			}						
		}
	}
	
	/**
	 * 当用户点击卖出时调用此函数实现卖出功能
	 * @param sellId
	 */
	public void doSell(int sellId) {
		//读取委托买入表t_buy中的信息到链表队列中
		List<Buy> buys = dao.query(Buy.class, Cnd.where("state", "<", DaoConst.STATE_FINISHIN).desc("price"));
		//如果t_buys表为空，则返回
		if (buys.size() == 0) {
			return;
		}
		
		Sell sell = dao.fetch(Sell.class, Cnd.where("id", "=", sellId));
		float sellPrice = sell.getPrice();
		int sellNum = sell.getShareNum() - sell.getFinishedShareNum();
		int sellerId = sell.getUserId();
	
		int i = 0;
		Buy buy = buys.get(i);
		float buyPrice = buy.getPrice();
		int finishedShareNum = buy.getFinishedShareNum();
		int buyNum = buy.getShareNum() - finishedShareNum;
		int buyerId = buy.getUserId();
		int buyId = buy.getId();
		
		float dealPrice;
		while (sellNum > 0 && sellPrice <= buyPrice) {
			dealPrice = (buyPrice + sellPrice)/2;
			if ( sellNum <= buyNum) {
				//t_sell(更新)--成交股数为sellNum
				sell.setFinishedShareNum(sell.getFinishedShareNum() + sellNum);
				sell.setState(DaoConst.STATE_FINISHIN);
				
				//t_buy(更新)
				buy.setFinishedShareNum(finishedShareNum + sellNum);
				if ( sellNum == buyNum) {
					buy.setState(DaoConst.STATE_FINISHIN);
				} else {
					buy.setState(DaoConst.STATE_PARTIAL);
				}
				
				//t_user(更新)
				//卖家更新
				User seller = dao.fetch(User.class, Cnd.where("id", "=", sellerId));
				seller.setFrozenNum(seller.getFrozenNum() - sellNum);
				seller.setUsableFund(seller.getUsableFund() + sellNum * dealPrice);
				//买家信息更新
				User buyer = dao.fetch(User.class, Cnd.where("id", "=", buyerId));
				buyer.setTradableNum(buyer.getTradableNum() + sellNum);
				//注意实际成交价格和委托价格之差对冻结资金的影响
				buyer.setFrozenFund(buyer.getFrozenFund() - sellNum * buyPrice);				
				buyer.setUsableFund(buyer.getUsableFund() + sellNum * (buyPrice - dealPrice));
				
				//t_deal(插入)				
				Deal deal = new Deal();
				deal.setBuyId(buyId);
				deal.setSellId(sellId);
				deal.setBuyerId(buyerId);
				deal.setSellerId(sellerId);
				deal.setTime(new Date());
				deal.setPrice(dealPrice);
				deal.setShareNum(sellNum);
				
				//操作数据库
				if (dao.update(buy) == 0 || dao.update(sell) == 0 || dao.update(buyer) == 0 
						|| dao.update(seller) == 0 || dao.insert(deal) == null) {
					this.writeError("数据库操作错误");
		            return;
				}
				
				//更新sellNum的值
				sellNum = sellNum - buyNum;
			} else if (sellNum > buyNum) {				
				//t_sell(更新)--成交股票数为buyNum
				sell.setFinishedShareNum(sell.getFinishedShareNum() + buyNum);
				sell.setState(DaoConst.STATE_PARTIAL);
				
				//t_buy(更新)
				buy.setFinishedShareNum(finishedShareNum + buyNum);
				buy.setState(DaoConst.STATE_FINISHIN);
				
				//t_user(更新)
				User seller = dao.fetch(User.class, Cnd.where("id", "=", sellerId));
				seller.setFrozenNum(seller.getFrozenNum() - buyNum);
				seller.setUsableFund(seller.getUsableFund() + buyNum * dealPrice);
				User buyer = dao.fetch(User.class, Cnd.where("id", "=", buyerId));
				buyer.setTradableNum(buyer.getTradableNum() + buyNum);
				buyer.setFrozenFund(buyer.getFrozenFund() - buyNum * buyPrice);
				buyer.setUsableFund(buyer.getUsableFund() + buyNum * (buyPrice - dealPrice));
				
				//t_deal(插入)				
				Deal deal = new Deal();
				deal.setBuyId(buyId);
				deal.setSellId(sellId);
				deal.setBuyerId(buyerId);
				deal.setSellerId(sellerId);
				deal.setTime(new Date());
				deal.setPrice(dealPrice);
				deal.setShareNum(sellNum);
				
				//操作数据库
				if (dao.update(buy) == 0 || dao.update(sell) == 0 || dao.update(buyer) == 0 
						|| dao.update(seller) == 0 || dao.insert(deal) == null) {
					this.writeError("数据库操作错误");
		            return;
				}
				
				//更新sellNum的值
				sellNum = sellNum - buyNum;
				
				//需要匹配t_buy中下一个词条
				i = i + 1;
				buy = buys.get(i);
				buyPrice = buy.getPrice();
				finishedShareNum = buy.getFinishedShareNum();
				buyNum = buy.getShareNum() - finishedShareNum;
				buyerId = buy.getUserId();
				buyId = buy.getId();			
			}
		}
	}
	
	/**
	 * 获取平台最近成交信息
	 */
	public void api_getDealInfo() {
		Sql sql = Sqls.queryEntity("SELECT time,price,sharenum FROM `t_deal` ORDER BY time DESC");
		sql.setEntity(dao.getEntity(Deal.class));
		dao.execute(sql);
		List<Deal> deals = sql.getList(Deal.class);
		
		//输出结果
		Map res = new HashMap();
		res.put("deals", deals);
		
		this.writeResult(res);
	}
}
