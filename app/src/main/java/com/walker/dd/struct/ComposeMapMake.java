package com.walker.dd.struct;

import java.util.Iterator;

import org.apache.log4j.PropertyConfigurator;

import com.walker.common.util.Context;
import com.walker.common.util.Tools;

/**
 * 数据结构 
 * 
 * 合成图 多权重
 * 
 * 如何存储？
 *
 */
public class ComposeMapMake implements MakeNode<Compose>{
	
	/**
	 * 载入数据库到内存
	 */
	public void initNode() {
		
	}
	/**
	 * 存储到内存并同步数据库
	 */
	public void saveNode(String name, String goldCost, int minuteCost, String rootNames) {
		
	}
	/**
	 * 获取内存节点
	 * @param name
	 * @return
	 */
	public Node<Compose> getNode(String name){
		
		return null;
	}
	
	Node<Compose> root = new Node<Compose>(new Compose("root", 0), 0, this);
	
	
	
	public  static void main(String[] argv) {
		MakeNode<Compose> make = new ComposeMapMake();
		Node<Compose> root = new Node<Compose>(new Compose("root", 0), 0, make);
		//root1
		//nimute:2
		//minuteTotal = 0 + minute
		Node<Compose> root1 = new Node<Compose>(new Compose("lv1", 10), 2, make);
		Node<Compose> root2 = new Node<Compose>(new Compose("lv2", 20), 5, make);
		root1.addParent(root, 0);
		root2.addParent(root, 0);

		//minute:10 
		Node<Compose> lv11_1 = new Node<Compose>(new Compose("lv11_1", 30), 10, make);
		//root1.num:1
		lv11_1.addParent(root1, 1);

		//minute:20
		Node<Compose> lv12_1_2 = new Node<Compose>(new Compose("lv12_1_2", 40), 20, make);
		//root1.num:1
		//root2.num:2
		//minuteTotal = root1.total * 1 + root2.total * 2 + minute
		lv12_1_2.addParent(root1, 1);
		lv12_1_2.addParent(root2, 2);

		Node<Compose> lv13_2 = new Node<Compose>(new Compose("lv13_2", 50), 30, make);
		lv13_2.addParent(root2, 3);


		

		
		
	}
	
	//构建自己
	public void make(Node<Compose> root) {
		int selfWeight = root.weight;
		Compose selfComp = root.value;
		selfComp.minuteCost = selfWeight;
		selfComp.minuteCostTotal = selfComp.minuteCost;
		
		Iterator<NodeWeight<Compose>> it = root.parents.iterator();
		while(it.hasNext()) {
			NodeWeight<Compose> item = it.next();
			Node<Compose> node = item.node;
			int aParentWeight = item.weight;
			Compose aParentComp = node.value;
			
			selfComp.minuteCostTotal += aParentComp.minuteCostTotal * aParentWeight;
			selfComp.goldCostTotal += aParentComp.goldCostTotal * aParentWeight;
		}
		Tools.out("构建:", root.toTreeString(0));
	}
	
	

}
