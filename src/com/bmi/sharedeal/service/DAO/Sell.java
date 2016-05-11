package com.bmi.sharedeal.service.DAO;

import java.util.Date;

import org.nutz.dao.entity.annotation.*;

@Table("t_sell")
public class Sell {
	@Id
	private int id;
	
	@Column("userid")
	private int userId;
	
	@Column("time")
	private Date time;
	
	@Column("buyorsell")
	private int buyOrSell;
	
	@Column("price")
	private float price;
	
	@Column("sharenum")
	private int shareNum;
	
	@Column("finishedsharenum")
	private int finishedShareNum;
	
	@Column("state")
	private int state;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getBuyOrSell() {
		return buyOrSell;
	}

	public void setBuyOrSell(int buyOrSell) {
		this.buyOrSell = buyOrSell;
	}
	
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getShareNum() {
		return shareNum;
	}

	public void setShareNum(int shareNum) {
		this.shareNum = shareNum;
	}

	public int getFinishedShareNum() {
		return finishedShareNum;
	}

	public void setFinishedShareNum(int finishedShareNum) {
		this.finishedShareNum = finishedShareNum;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
