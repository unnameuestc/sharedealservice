package com.bmi.sharedeal.service.DAO;

import org.nutz.dao.entity.annotation.*;
@Table("t_debtinfo")
public class DebtInfo {
	@Id
	private int id;
	@Column("name")
	private String name;
	@Column("price")
	private float price;
	@Column("phone")
	private int phone;
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
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getPhone() {
		return phone;
	}
	public void setPhone(int phone) {
		this.phone = phone;
	}
	
}
