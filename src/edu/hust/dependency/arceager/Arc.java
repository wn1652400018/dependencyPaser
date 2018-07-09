package edu.hust.dependency.arceager;

import java.util.ArrayList;

import edu.hust.dependency.DependencyTree;

public class Arc {
	private Relation relation;
	private Vertice head;
	private Vertice dependent;
	

	public Relation getRelation() {
		return relation;
	}
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	public Vertice getHead() {
		return head;
	}
	public void setHead(Vertice head) {
		this.head = head;
	}
	public Vertice getDependent() {
		return dependent;
	}
	public void setDependent(Vertice dependent) {
		this.dependent = dependent;
	}
}
