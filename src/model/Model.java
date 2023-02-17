package model;

import java.util.*;
import lang.*;
import java.io.*;

public class Model {
	private HashMap<ModelState, HashSet<ModelState>> succList; // Succesors adjacency list
	private HashMap<ModelState, HashSet<ModelState>> preList; // Predecessors adjacency list
	private HashMap<Pair, ArrayList<Action>> actions; // Edge actions
	private ModelState initial; // Initial State
	private LinkedList<Var> sharedVars; // Global variables
	private ArrayList<ModelState> nodes; // Global states
	private int numNodes;
	private int numEdges;
	private LinkedList<Proc> procs;
	private LinkedList<String> procDecls;
	private boolean isWeak;
	private boolean isSpec;
	private HashMap<String,Object> constants;

	public Model(GlobalVarCollection svs, boolean isS) {
		sharedVars = svs.getBoolVars();
		sharedVars.addAll(svs.getEnumVars());
		sharedVars.addAll(svs.getIntVars());
		succList = new HashMap<ModelState, HashSet<ModelState>>();
		preList = new HashMap<ModelState, HashSet<ModelState>>();
		actions = new HashMap<Pair, ArrayList<Action>>();
		numNodes = numEdges = 0;
		nodes = new ArrayList<ModelState>();
		procs = new LinkedList<Proc>();
		procDecls = new LinkedList<String>();
		isWeak = false;
		isSpec = isS;
	}

	public void setInitial(ModelState v){
		initial = v;
	}

	public void setIsWeak(boolean b){
		isWeak = b;
	}

	public boolean getIsWeak(){
		return isWeak;
	}

	public ModelState getInitial(){
		return initial;
	}

	public LinkedList<Var> getSharedVars(){
		return sharedVars;
	}

	public HashMap<String,Object> getConstants(){
		return constants;
	}

	public HashMap<Pair, ArrayList<Action>> getActions(){
		return actions;
	}

	public LinkedList<Proc> getProcs(){
		return procs;
	}

	public LinkedList<String> getProcDecls(){
		return procDecls;
	}

	public int getNumNodes(){
		return numNodes;
	}

	public int getNumEdges(){
		return numEdges;
	}


	public void addNode(ModelState v) {
		nodes.add(v);
		succList.put(v, new HashSet<ModelState>());
		preList.put(v, new HashSet<ModelState>());
		numNodes += 1;
	}

	public ModelState search(ModelState v) {
		for (ModelState node : nodes){
			if (node.equals(v))
				return node;
		};
		return null;
	}

	public boolean hasNode(ModelState v) {
		return nodes.contains(v);
	}


	public boolean hasEdge(ModelState from, ModelState to, Action a) {

		if (!hasNode(from) || !hasNode(to))
			return false;
		Pair transition = new Pair(from,to);
		if (actions.get(transition) == null){
			return false;
		}
		
		return actions.get(transition).contains(a);
	}


	public boolean addEdge(ModelState from, ModelState to, Action a) {
		if (to != null){
			if (a.isTau() && a.getLabel().charAt(0)!='&')
				a.setLabel("&"+a.getLabel());
			if (hasEdge(from, to, a))
				return false;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
			Pair transition = new Pair(from,to);
			if (actions.get(transition) == null){
				actions.put(transition,new ArrayList<Action>());
			}
			actions.get(transition).add(a);
			return true;
		}
		return false;
	}

	public ArrayList<ModelState> getNodes(){
		return nodes;
	}

	public HashSet<ModelState> getSuccessors(ModelState v){
		return succList.get(v);
	}

	public HashSet<ModelState> getPredecessors(ModelState v){
		return preList.get(v);
	}

	public String createDot(boolean isImp){
		String res = "digraph model {\n\n";
		for (ModelState v : nodes){
			for (ModelState u : succList.get(v)){
				Pair edge = new Pair(v,u);
				if (actions.get(edge) != null)
					for (int i=0; i < actions.get(edge).size(); i++){
						if (actions.get(edge).get(i).isFaulty())
							res += "    STATE"+v.toStringDot()+" -> STATE"+ u.toStringDot() +" [color=\"red\",label = \""+actions.get(edge).get(i).getLabel()+"\"]"+";\n";
						else
							if (actions.get(edge).get(i).isTau())
								res += "    STATE"+v.toStringDot()+" -> STATE"+ u.toStringDot() +" [style=dashed,label = \""+actions.get(edge).get(i).getLabel()+"\"]"+";\n";
							else
								res += "    STATE"+v.toStringDot()+" -> STATE"+ u.toStringDot() +" [label = \""+actions.get(edge).get(i).getLabel()+"\"]"+";\n";
					}
			}
		}
		res += "\n}";
		try{
			String path = "";
			if (isImp)
            	path ="../out/" + "ImpModel" +".dot";
            else
            	path ="../out/" + "SpecModel" +".dot";
            File file = new File(path);
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


	public void saturate(){
		//Add tau self-loops
		if (!isWeak)
			return;
		for (ModelState p : nodes){
			addEdge(p,p,new Action("&",false,true,isSpec)); // p -> p is internal
		}
		boolean change = true;
		//this lists will share the same size
		ArrayList<ModelState> fsts;
		ArrayList<ModelState> snds;
		ArrayList<String> lbls;
		ArrayList<Boolean> isTaus;


		//Saturate graph
		while (change){
			change = false;
			fsts = new ArrayList<ModelState>();
			snds = new ArrayList<ModelState>();
			lbls = new ArrayList<String>();
			isTaus = new ArrayList<Boolean>();

			for (ModelState p : nodes){
				for (ModelState p_ : succList.get(p)){
					Pair t0 = new Pair(p,p_);
					if (actions.get(t0) != null){
						for (int i = 0; i < actions.get(t0).size(); i++){
							if (actions.get(t0).get(i).isTau()){ // p -> p_ is internal
								for (ModelState q_ : succList.get(p_)){
									Pair t1 = new Pair(p_,q_);
									for (int j = 0; j < actions.get(t1).size(); j++){
										String lbl = actions.get(t1).get(j).getLabel();
										Boolean isF = actions.get(t1).get(j).isFaulty();
										Boolean isTau = actions.get(t1).get(j).isTau();
										if (!isF){ //don't saturate faulty actions
											for (ModelState q : succList.get(q_)){
												Pair t2 = new Pair(q_,q);
												if (actions.get(t2) != null){
													for (int k = 0; k < actions.get(t2).size(); k++){
														if (actions.get(t2).get(k).isTau()){ // q_ -> q is internal
															//add transition for later update
															if (!hasEdge(p,q,actions.get(t1).get(j))){
																fsts.add(p);
																snds.add(q);
																lbls.add(lbl);
																isTaus.add(isTau);
																change = true;		
															}	
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			//update transition system
			for (int i = 0; i < fsts.size(); i++){
				addEdge(fsts.get(i), snds.get(i), new Action(lbls.get(i), false, isTaus.get(i), isSpec));
			}

		}
		
	}
	
}