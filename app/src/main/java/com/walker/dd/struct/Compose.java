package com.walker.dd.struct;

/**
 * 合成树
 */
public class Compose {
	/**
	 * 名称
	 */
	String name;
	public Compose(String name,  int goldCost) {
		this.name = name;
//		this.minuteCost = minuteCost;
		this.goldCost = goldCost;
	}
	
	/**
	 * 合成耗时
	 */
	int minuteCost=0;
	/**
	 * 合成消耗物品价值gold
	 */
	int goldCost=0;
	/**
	 * 合成耗时 总计 线路和
	 */
	int minuteCostTotal=0;
	/**
	 * 合成消耗gold 总计 线路和
	 */
	int goldCostTotal=0;
	/**
	 * 说明
	 */
	String about="";
	
	
	
	
	
	
	@Override
	public boolean equals(Object obj) {
		Compose comp = (Compose) obj;
		return comp.name.equals(this.name);
	}
	@Override
	public String toString() {
		return name + " min:" + minuteCost + " minT:" + minuteCostTotal + " go:" + goldCost + " goT" + goldCostTotal + " " + about;//JsonUtil.makeJson(LangUtil.turnObj2Map(this));
	}
	
	
}
