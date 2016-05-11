package com.bmi.sharedeal.service.DAO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.entity.annotation.*;

@Table("t_user")
public class User {
	@Id
	private int id;
	
	@Column("name")
	private String name;
	
	@Column("password")
	private String password;
	
	@Column("tradablenum")
	private int tradableNum;
	
	@Column("frozennum")
	private int frozenNum;
	
	@Column("usablefund")
	private float usableFund;
	
	@Column("frozenfund")
	private float frozenFund;

	@Column("logintime")
	private Date loginTime;
		
	@Column("authkey")
    private String authKey;	
	
	@Column("adminid")
	private int adminId;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTradableNum() {
		return tradableNum;
	}

	public void setTradableNum(int tradableNum) {
		this.tradableNum = tradableNum;
	}
	
	public int getFrozenNum() {
		return frozenNum;
	}

	public void setFrozenNum(int frozenNum) {
		this.frozenNum = frozenNum;
	}
	
	public float getUsableFund() {
		return usableFund;
	}

	public void setUsableFund(float usableFund) {
		this.usableFund = usableFund;
	}

	public float getFrozenFund() {
		return frozenFund;
	}

	public void setFrozenFund(float frozenFund) {
		this.frozenFund = frozenFund;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	
	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public Map toKVPair() {
        Map map = new HashMap();

        map.put("id", id);
        map.put("name", name);
        map.put("tradablenum", tradableNum);
        map.put("frozennum", frozenNum);
        map.put("usablefund", usableFund);
        map.put("frozenfund", frozenFund);
        map.put("logintime", loginTime);
        map.put("adminid", adminId);
        
        return map;
    }
	
}
