package games;

import java.util.*;
import model.*;
import java.io.*;

public class GameGraph{

	private HashMap<GameNode, HashSet<GameNode>> succList; // Successors adjacency list
	private HashMap<GameNode, HashSet<GameNode>> preList; // Predecessors adjacency list
	private HashMap<Pair, LinkedList<Action>> actions; // actions for edges
	private GameNode initial; // Initial state
	private LinkedList<GameNode> nodes; // States
	private int numNodes;
	private int numEdges;
	private GameNode errState; // Special error state

	public GameGraph() {
		succList = new HashMap<GameNode, HashSet<GameNode>>();
		preList = new HashMap<GameNode, HashSet<GameNode>>();
		actions = new HashMap<Pair, LinkedList<Action>>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<GameNode>();
		errState = new GameNode(null,null,new Action("ERR", false, false, false),"");
		addNode(errState);

	}


	public GameNode getErrState(){		
		return errState;
	}

	public int getNumNodes(){
		return numNodes;
	}

	public int getNumEdges(){
		return numEdges;
	}

	public void setInitial(GameNode v){
		initial = v;
	}

	public GameNode getInitial(){
		return initial;
	}

	public HashMap<Pair, LinkedList<Action>> getActions(){
		return actions;
	}

	public void addNode(GameNode v) {
		nodes.add(v);
		succList.put(v, new HashSet<GameNode>());
		preList.put(v, new HashSet<GameNode>());
		numNodes += 1;
	}

	public GameNode search(GameNode v) {
		for (GameNode node : nodes){
			if (node.equals(v))
				return node;
		}
		return null;
	}

	public boolean hasNode(GameNode v) {
		return nodes.contains(v);
	}


	public boolean hasEdge(GameNode from, GameNode to, Action a) {
		if (!hasNode(from) || !hasNode(to))
			return false;
		Pair transition = new Pair(from,to);
		if (actions.get(transition) == null)
			return false;
		return actions.get(transition).contains(a);
	}


	public void addEdge(GameNode from, GameNode to, Action a) {
		if (to != null){
			if (hasEdge(from, to, a))
				return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
			Pair transition = new Pair(from,to);
			if (actions.get(transition) == null){
				actions.put(transition, new LinkedList<Action>());
			}
			actions.get(transition).add(a);
		}
	}

	public LinkedList<GameNode> getNodes(){
		return nodes;
	}

	public HashSet<GameNode> getSuccessors(GameNode v){
		return succList.get(v);
	}

	public HashSet<GameNode> getPredecessors(GameNode v){
		return preList.get(v);
	}

	public String createDot(int lineLimit, String name, boolean debugMode){
		String res = "";
		LinkedList<GameNode> ns;
		if (debugMode){
			ns = new LinkedList<GameNode>();
			ns.add(errState);
			ns.addAll(getPredecessors(errState));
			LinkedList<GameNode> ns_ = new LinkedList<GameNode>();
			for (int i=0; i<ns.size(); i++){
				ns_.addAll(getPredecessors(ns.get(i)));
			}
			ns.addAll(ns_);
		}
		else{
			ns = nodes;
		}
		res = "digraph model {\n\n";
		res += "    node [style=filled];\n";
		int lineCount = 0;
		for (GameNode v : ns){
			//if (lineCount > lineLimit)
			//	break;
			lineCount++;
			if (v.getPlayer().equals("V"))
				res += "    "+ v.getId() +" [label=\""+v.toStringDot()+"\",color=\"lightblue\"];\n";
			if (v.getPlayer().equals("P"))
				res += "    "+ v.getId() +" [label=\""+v.toStringDot()+"\",color=\"orange\"];\n";
			if (v.getPlayer().equals("R"))
				if (v == initial)
					res += "    "+ v.getId() +" [label=\""+v.toStringDot()+"\",color=\"pink\"];\n";
				else
					res += "    "+ v.getId() +" [label=\""+v.toStringDot()+"\",color=\"grey\"];\n";
			if (v.getPlayer().equals(""))
				res += "    "+v.getId()+" [label=\""+v.toStringDot()+"\",color=\"red\"];\n";
			for (GameNode u : succList.get(v)){
				Pair edge = new Pair(v,u);
				if (actions.get(edge) != null)
					for (int i=0; i < actions.get(edge).size(); i++){
						if (actions.get(edge).get(i).isMask())
							res += "    "+v.getId()+" -> "+ u.getId() +" [color=\"green\"]"+";\n";
						else
							if (actions.get(edge).get(i).isFaulty())
								res += "    "+v.getId()+" -> "+ u.getId() +" [color=\"red\"]"+";\n";
							else
								if (actions.get(edge).get(i).isTau())
									res += "    "+v.getId()+" -> "+ u.getId() + " [style=dashed]"+";\n";
								else
									res += "    "+v.getId()+" -> "+ u.getId() + ";\n";
					}			
			}
		}
		res += "\n}";
		try{
            File file = new File("../out/" + name +".dot");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(res);
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
		return res;
	}
}