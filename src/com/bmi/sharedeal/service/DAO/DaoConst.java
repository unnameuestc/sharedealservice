package com.bmi.sharedeal.service.DAO;

/**
 * 
 * @author xiaoyu
 *
 */
public class DaoConst {
	public static final int STATE_INITIAL = 0;	//初始交易状态
	public static final int STATE_PARTIAL = 1;	//部分完成状态
	public static final int STATE_FINISHIN = 2;	//全部完成状态
	public static final int STATE_REVOCATION = 3;	//撤销委托
	
	public static final int BUYORSELL_BUY = 0;		//买入
	public static final int BUYORSELL_SELL = 1;		//卖出
	
	public static final int DEALNUM_BUY = 5;		//平台最近买入显示词条数
	public static final int DEALNUM_SELL = 5;		//平台最近卖出显示词条数
	

}
