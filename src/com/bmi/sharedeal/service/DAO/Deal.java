package com.bmi.sharedeal.service.DAO;

import java.util.Date;

import org.nutz.dao.entity.annotation.*;

@Table("t_deal")
public class Deal {
	@Id
	private int id;
	
	@Column("buyid")
	private int buyId;
	
	@Column("sellid")
	private int sellId;
	
	@Column("buyerid")
	private int buyerId;
	
	@Column("sellerid")
	private int sellerId;
	
	@Column("time")
	private Date time;
	
	@Column("price")
	private float price;
	
	@Column("sharenum")
	private int shareNum;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBuyId() {
		return buyId;
	}

	public void setBuyId(int buyId) {
		this.buyId = buyId;
	}

	public int getSellId() {
		return sellId;
	}

	public void setSellId(int sellId) {
		this.sellId = sellId;
	}	

	public int getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
	}

	public int getSellerId() {
		return sellerId;
	}

	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
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

}
