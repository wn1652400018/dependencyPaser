package edu.hust.dependency.arceager;

public class ActionType {
	//暂时认为是分类的结构，将来在重新设计
	private Relation relation;
	private String baseAction;//LEFTARC、RIGHTARC···
	public ActionType(Relation relation,String baseAction) {
		this.relation = relation;
		this.baseAction = baseAction;
	}
	
	public ActionType() {}
	
	public Relation getRelation() {
		return relation;
	}
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	public String getBaseAction() {
		return baseAction;
	}
	public void setBaseAction(String baseAction) {
		this.baseAction = baseAction;
	}
	
}
