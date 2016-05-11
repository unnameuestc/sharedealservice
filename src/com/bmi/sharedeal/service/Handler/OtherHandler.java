package com.bmi.sharedeal.service.Handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;

import com.bmi.sharedeal.service.DAO.Admin;
import com.bmi.sharedeal.service.DAO.Buy;
import com.bmi.sharedeal.service.DAO.DaoConst;
import com.bmi.sharedeal.service.DAO.Deal;
import com.bmi.sharedeal.service.DAO.Sell;
import com.bmi.sharedeal.service.DAO.User;
import com.bmi.sharedeal.service.Server.BaseHandler;
import com.bmi.sharedeal.service.utils.TextUtils;

public class OtherHandler extends BaseHandler{
	public void api_adminLogin() {
		String userName = (String) this.getArgument("username");
		String pwd = (String) this.getArgument("pwd");
		
		System.out.println("pwd: " + pwd);
		
		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
			this.writeError("参数不足");
		}
		
		Admin admin = dao.fetch(Admin.class, Cnd.where("name", "=", userName).and("password", "=", pwd));
		if (admin == null) {
			this.writeError("用户名或密码错误");
            return;
		}
		
		//生成authkey
		String seed = admin.getName() + new Date().toString() + new Random().nextInt(1000);
		String md5 = TextUtils.MD5(seed);
		
		if (TextUtils.isEmpty(md5)) {
            this.writeError("服务器内部错误");
            return;
        }
		
		admin.setAuthKey(md5);
		if (dao.update(admin) <=0 ) {
			this.writeError("数据库错误");
			return;
		}
		
		Map res = new HashMap();
        res.put("authKey", admin.getAuthKey());
        this.writeResult(res);
	}

	public void api_adminLogout() {
		String authkey = (String) this.getArgument("authkey");
		if (TextUtils.isEmpty(authkey)) {
			this.writeError("参数不足");
			return;
		}
		
		Admin admin = dao.fetch(Admin.class, Cnd.where("authkey", "=", authkey));
		if (admin == null) {
			this.writeError("管理员验证失败");
		}
		
		admin.setAuthKey(null);
		
		if (dao.update(admin) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
	}

	public void api_addUser() {
		String authkey = (String) this.getArgument("authkey");
		if (TextUtils.isEmpty(authkey)) {
			this.writeError("请登录");
			return;
		}
		
		Admin admin = dao.fetch(Admin.class, Cnd.where("authkey", "=", authkey));
		if (admin == null) {
			this.writeError("管理员验证失败");
		}
		
		String userName = (String) this.getArgument("username");
		String passWord = (String) this.getArgument("pwd");
		String _tradableNum = (String) this.getArgument("tradablenum");
		String _frozenNum = (String) this.getArgument("frozennum");
		String _usableFund = (String) this.getArgument("usablefund");
		String _frozenFund = (String) this.getArgument("frozenfund");
		
//		System.out.println(userName + " " + passWord + " " + _tradableNum + " " + _frozenNum + " " + _usableFund + " " + _frozenFund);
		
		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord) || TextUtils.isEmpty(_tradableNum)
				|| TextUtils.isEmpty(_frozenNum) || TextUtils.isEmpty(_usableFund) ||TextUtils.isEmpty(_frozenFund)) {
			this.writeError("参数不足");
			return;
		}
		
		String md5PWD = TextUtils.MD5(passWord);
		int tradableNum = Integer.parseInt(_tradableNum);
		int frozenNum = Integer.parseInt(_frozenNum);
		int usableFund = Integer.parseInt(_usableFund);
		int frozenFund = Integer.parseInt(_frozenFund);
		
		User u = new User();
		u.setName(userName);
		u.setPassword(md5PWD);
		u.setTradableNum(tradableNum);
		u.setFrozenNum(frozenNum);
		u.setUsableFund(usableFund);
		u.setFrozenFund(frozenFund);
		u.setAdminId(admin.getId());
		
		if (dao.insert(u) == null) {
			this.writeError("数据库错误");
			return;
		}
		
		Map res = new HashMap();
		res.put("user", u);
		this.writeResult(res);
	}
	
	/**
	 * 获取平台所有成交信息
	 */
	public void api_getAllDealInfo() {
		String _page = (String) this.getArgument("page");		
		
		if( TextUtils.isEmpty(_page)) {
			this.writeError("参数不足");
			return;
		}
		
		int page = Integer.parseInt(_page);						
		
		Pager pager = dao.createPager(page, 10);
		List<Deal> deals = dao.query(Deal.class, Cnd.orderBy().desc("time"), pager);
		int allCnt = dao.count(Deal.class);
		
		//输出结果
		Map res = new HashMap();
		res.put("deals", deals);
		res.put("cnt", deals.size());
		res.put("allCnt", allCnt);
		
		this.writeResult(res);
	}
	
	/**
	 * 查询所有委托买入信息
	 */
	
	public void api_getAllBuyInfo() {
		String _page = (String) this.getArgument("page");		
		
		if( TextUtils.isEmpty(_page)) {
			this.writeError("参数不足");
			return;
		}
		
		int page = Integer.parseInt(_page);						
		
		Pager pager = dao.createPager(page, 10);
		List<Buy> buys = dao.query(Buy.class, null, pager);
		int allCnt = dao.count(Buy.class);
		
		//输出结果
		Map res = new HashMap();
		res.put("buys", buys);
		res.put("cnt", buys.size());
		res.put("allCnt", allCnt);
		
		this.writeResult(res);
	}
	
	/**
	 * 查询所有委托卖出信息
	 */
	
	public void api_getAllSellInfo() {
		String _page = (String) this.getArgument("page");		
		
		if( TextUtils.isEmpty(_page)) {
			this.writeError("参数不足");
			return;
		}
		
		int page = Integer.parseInt(_page);						
		
		Pager pager = dao.createPager(page, 10);
		List<Sell> sells = dao.query(Sell.class, null, pager);
		int allCnt = dao.count(Sell.class);
		
		//输出结果
		Map res = new HashMap();
		res.put("sells", sells);
		res.put("cnt", sells.size());
		res.put("allCnt", allCnt);
		
		this.writeResult(res);
	}
}
