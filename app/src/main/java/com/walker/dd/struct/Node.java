package com.walker.dd.struct;

import java.util.*;

import com.walker.common.util.Tools;

/**
 * 数据结构 
 * 
 * 双向多链表
 * 
 * 该节点由多个 parent节点构成 线的权重 = parent线路和
 * 
 * 每个节点只管它的子节点
 * 父操作交由父节点
 * 
 */
public class Node<T> {
	/**
	 * 值
	 */
	T value;
	
	/**
	 * 权 1 minute
	 */
	int weight;
	
	/**
	 * 回调构建self
	 */
	MakeNode<T> makeNode;
	
	public Node(T value, int weight, MakeNode<T> makeNode) {
		this.value = value;
		this.weight = weight;
		this.makeNode = makeNode;
	}
	/**
	 * 父节点s 
	 */
	Set<NodeWeight<T>> parents = new HashSet<>();

	/**
	 * 子节点s
	 */
	Set<NodeWeight<T>> childs = new HashSet<>();
	
	public Node<T> addParent(Node<T> node, int weight) {
		NodeWeight<T> nwParent = new NodeWeight<T>(node, weight);
		NodeWeight<T> nwThis = new NodeWeight<T>(this, weight);
		parents.add(nwParent);
		node.childs.add(nwThis);
		
		this.makeNode(this);
		return this;
	}
	public Node<T> removeParent(Node<T> node) {
		NodeWeight<T> nwParent = new NodeWeight<T>(node, weight);
		NodeWeight<T> nwThis = new NodeWeight<T>(this, weight);
		parents.remove(nwParent);
		node.childs.remove(nwThis);

		this.makeNode(this);
		return this;
	} 
	
	@Override
	public boolean equals(Object obj) {
		Node<T> node = (Node<T>) obj;
		return node.value.equals(this.value);
	}
	@Override
	public String toString() {
		return String.valueOf(this.value) + ":" + this.weight;
	}
	public String toTreeString(int i) {
		i++;
		String tab = " \n" + Tools.fillStringBy("", " ", i * 4, 0);
		String res = tab + this.toString();
		Iterator<NodeWeight<T>> it = this.parents.iterator();
		while(it.hasNext()) {
			NodeWeight<T> item = it.next();
			res += item.toTreeString(i);
		}
		return res;
	}
	
	

	/**
	 * 从root依次构建带权 树?
	 * 构建树 广度优先 
	 */
	public void makeNode(Node<T> root) {
		if(makeNode != null) {
			makeNode.make(root);
		}
		makeNodeChild(root);
	}
	//构建子节点
	public void makeNodeChild(Node<T> root) {
		Iterator<NodeWeight<T>> it = root.childs.iterator();
		while(it.hasNext()) {
			NodeWeight<T> item = it.next();
			Node<T> node = item.node;

			if(makeNode != null) {
				makeNode.make(node);
			}		
		}
		it = root.childs.iterator();
		while(it.hasNext()) {
			NodeWeight<T> item = it.next();
			Node<T> node = item.node;
			makeNodeChild(node);
		}
	}
	

}

class NodeWeight<T>{
	Node<T> node;
	/**
	 * 组合list权2
	 */
	int weight;
	public NodeWeight(Node<T> node, int weight) {
		this.node = node;
		this.weight = weight;
	}
	public String toTreeString(int i) {
		return node.toTreeString(i) + ":" + weight;
	}
	@Override
	public String toString() {
		return String.valueOf(this.node) + ":" + this.weight;
	}
	
	@Override
	public boolean equals(Object obj) {
		NodeWeight<T> node = (NodeWeight<T>) obj;
		return node.node.equals(this.node);
	}
}

interface MakeNode<T>{
	public void make(Node<T> node);
}