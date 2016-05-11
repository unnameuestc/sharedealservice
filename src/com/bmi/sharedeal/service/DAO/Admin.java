package com.bmi.sharedeal.service.DAO;

import java.util.Date;

import org.nutz.dao.entity.annotation.*;

@Table("t_admin")
public class Admin {
	@Id
	private int id;
	
	@Column("name")
	private String name;
	
	@Column("password")
	private String password;
	
	@Column("logintime")
	private Date logintime;

	@Column("authkey")
    private String authKey;

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

	public Date getLogintime() {
		return logintime;
	}

	public void setLogintime(Date logintime) {
		this.logintime = logintime;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}	
	
	
	
}
