package edu.hust.dependency.arceager;

import java.util.ArrayList;

public class ActionTypeArcEagerBased extends ArrayList<ActionType> {
	private static ActionTypeArcEagerBased allActions;

	private ActionTypeArcEagerBased() {}

	public static ActionTypeArcEagerBased getInstance() {
		if (allActions == null) {
			allActions.add(new ActionType(new Relation(), "LEFTARC"));
			// ..............................
			// ..............................
			// ..............................
			return allActions;
		} else
			return allActions;

	}
}
