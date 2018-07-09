package edu.hust.dependency.mem;

import java.util.ArrayList;
import java.util.List;

import edu.hust.dependency.arceager.ActionType;
import edu.hust.dependency.arceager.Arc;
import edu.hust.dependency.arceager.Configuration;
import edu.hust.dependency.arceager.OperatorArcEagerbased;
import edu.hust.dependency.arceager.Oracle;
import edu.hust.dependencyParse.DependencyParser;
import edu.hust.dependencyParse.DependencyTree;

public class DependencyParserMEM implements DependencyParser{

	@Override
	public DependencyTree parse(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DependencyTree parse(String[] words, String[] poses) {
		// TODO Auto-generated method stub
		Oracle oracleMEBased = new Oracle();
		OperatorArcEagerbased operator = new OperatorArcEagerbased();
		ActionType action;
		List<Configuration> confList = new ArrayList<Configuration>();
		Configuration initialConf = new Configuration(words, poses);
		confList.add(initialConf);
		while (confList.get(confList.size() - 1).isFinalConf()) {
			action = oracleMEBased.classifyConf(confList.get(confList.size() - 1));
			Configuration nextConf = operator.transitionOperate(confList.get(confList.size() - 1), action);
			confList.add(nextConf);
		}
		Configuration finalConf = confList.get(confList.size() - 1);
		DependencyTree depTree = getDePendencyTree(finalConf.getArcs());
		return depTree;
	}

	@Override
	public DependencyTree[] parse(String sentence, int k) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DependencyTree[] parse(String[] words, String[] poses, int k) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public  DependencyTree getDePendencyTree(ArrayList<Arc> arcs) {
		//根据arcs列表获得依存树
		return new DependencyTree();
	}
	
	
}
