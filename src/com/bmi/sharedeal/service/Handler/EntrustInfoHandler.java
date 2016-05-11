package com.bmi.sharedeal.service.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;

import com.bmi.sharedeal.service.DAO.Buy;
import com.bmi.sharedeal.service.DAO.DaoConst;
import com.bmi.sharedeal.service.DAO.Deal;
import com.bmi.sharedeal.service.DAO.Sell;
import com.bmi.sharedeal.service.DAO.User;
import com.bmi.sharedeal.service.Server.BaseHandler;
import com.bmi.sharedeal.service.utils.TextUtils;

/**
 * 
 * @author xiaoyu
 *
 */

public class EntrustInfoHandler extends BaseHandler{
	/**
	 * 获取已登录用户的委托交易信息
	 */
	public void api_getEntrustInfo() {
		//判断是否登录
		User user = UserHandler.checkUserAuth(this);
		if(user == null) {
			return;
		}
		
		String _page = (String) this.getArgument("page");
		String _pageSize = (String) this.getArgument("pagesize");
		
		if( TextUtils.isEmpty(_page) || TextUtils.isEmpty(_pageSize)) {
			this.writeError("参数不足");
			return;
		}
		
		int page = Integer.parseInt(_page);				
		int pageSize = Integer.parseInt(_pageSize);
		
		int userId = user.getId();
		
		//如何拼接buys和sells链表，并实现分页和按时间排序
		Sql sql = Sqls.queryEntity("SELECT * FROM t_buy WHERE userid = '" + userId 
				+ "' UNION SELECT * FROM t_sell WHERE userid = ' " + userId + "' ORDER BY time DESC");
		sql.setPager(dao.createPager(page, pageSize));
		sql.setEntity(dao.getEntity(Buy.class));
		dao.execute(sql);
		List<Buy> entrusts = sql.getList(Buy.class);

		//计算查询结果集的词条总数????(更高效的方法)
		Sql _sql = Sqls.queryEntity("SELECT * FROM t_buy WHERE userid = '" + userId 
				+ "' UNION SELECT * FROM t_sell WHERE userid = ' " + userId + "' ORDER BY time DESC");
		_sql.setEntity(dao.getEntity(Buy.class));
		dao.execute(_sql);
		List<Buy> _entrusts = _sql.getList(Buy.class);
		
		//输出结果
		Map res = new HashMap();
		
		res.put("entrusts", entrusts);
		res.put("Cnt", _entrusts.size());
		
		this.writeResult(res);		
	}
	
	/**
	 * 获取已登录用户的成交信息
	 */
	public void api_getDealInfo() {
		//判断是否登录
		User user = UserHandler.checkUserAuth(this);
		if(user == null) {
			return;
		}
		
		String _page = (String) this.getArgument("page");
		String _pageSize = (String) this.getArgument("pagesize");
		
		if( TextUtils.isEmpty(_page) || TextUtils.isEmpty(_pageSize)) {
			this.writeError("参数不足");
			return;
		}
		
		int page = Integer.parseInt(_page);				
		int pageSize = Integer.parseInt(_pageSize);
		
		int userId = user.getId();
		
		Pager pager = dao.createPager(page, pageSize);
		List<Deal> deals = dao.query(Deal.class, Cnd.where("buyerid", "=", userId).or("sellerid", "=", userId).desc("time"), pager);
		int cnt = deals.size();
		int pageCnt = cnt / pageSize + 1;
		//输出结果
		Map res = new HashMap();
		
		res.put("deals", deals);
		res.put("Cnt", cnt);
		res.put("pageCnt", pageCnt);
		
		this.writeResult(res);		
	}

	/**
	 * 已登录用户撤销委托信息
	 * @param id,buyorsell
	 */
	public void api_repealEntrust() {
		//判断是否登录
		User user = UserHandler.checkUserAuth(this);
		if(user == null) {
			return;
		}
		
		String _entrustId = (String) this.getArgument("entrustid");
		String _buyorsell = (String) this.getArgument("buyorsell");
		
		if( TextUtils.isEmpty(_entrustId) || TextUtils.isEmpty(_buyorsell)) {
			this.writeError("参数不足");
			return;
		}
		
		int entrustId = Integer.parseInt(_entrustId);
		int buyorsell = Integer.parseInt(_buyorsell);
		float repealFund;
		int repealNum;
					
		if (buyorsell == 0) {
			Buy entrust = dao.fetch(Buy.class, Cnd.where("id", "=", entrustId));
			repealFund = entrust.getPrice() * (entrust.getShareNum() - entrust.getFinishedShareNum());
			
			if (entrust.getState() >= 2) {
				return;
			}
			
			entrust.setState(DaoConst.STATE_REVOCATION);
			user.setFrozenFund(user.getFrozenFund() - repealFund);
			user.setUsableFund(user.getUsableFund() + repealFund);
						
			if (dao.update(user) <= 0 || dao.update(entrust) <= 0) {				
				this.writeError("数据库错误");
				return;
			}
			
			Map res = new HashMap();			
			res.put("entrust", entrust);
			this.writeResult(res);
		} else if (buyorsell == 1) {
			Sell entrust = dao.fetch(Sell.class, Cnd.where("id", "=", entrustId));
			repealNum = entrust.getShareNum() - entrust.getFinishedShareNum();
			
			if (entrust.getState() >= 3) {
				return;
			}
			
			user.setFrozenNum(user.getFrozenNum() - repealNum);
			user.setTradableNum(user.getTradableNum() + repealNum);
			entrust.setState(DaoConst.STATE_REVOCATION);
			
			if (dao.update(user) <= 0 || dao.update(entrust) <=0) {
				this.writeError("数据库错误");
				return;
			}
			
			Map res = new HashMap();			
			res.put("entrust", entrust);
			this.writeResult(res);
		}
	}
}
