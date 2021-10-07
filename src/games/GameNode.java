package games;

import java.util.*;
import model.*;

public class GameNode implements Comparable<Object>{

	ModelState specState; // Current state of the Specification
	ModelState impState; // Current state of the Implementation
	Action symbol; // The action that lead to this state
	String player; // The player that has to play from here
	boolean mask; // True if the player has to mask this.symbol
	boolean visited; // Utility for graph traversal algorithms
	int distanceValue; // Value of the game for this node
	GameNode previousNodeInPath; // Previous Node in Shortest path to errState
	boolean numbered; // True if marked with a temp mask distance
	int id;
	private static int idCounter = 0;
	//EXPANSION SET
	Double[] values; // for value iteration

	public GameNode(){
		visited = false;
		distanceValue = 0;
		values = new Double[2];
		id = idCounter++;
	}

	public GameNode(ModelState s, ModelState i, Action sym, String p){
		specState = s;
		impState = i;
		symbol = sym;
		player = p;
		visited = false;
		distanceValue = Integer.MAX_VALUE;
		numbered = false;
		values = new Double[2];
		id = idCounter++;
	}

	public ModelState getSpecState(){
		return specState;
	}

	public ModelState getImpState(){
		return impState;
	}

	public Action getSymbol(){
		return symbol;
	}

	public String getPlayer(){
		return player;
	}

	public boolean getMask(){
		return mask;
	}

	public int getId(){
		return id;
	}

	public void setMask(boolean m){
		mask = m;
	}

	public boolean getVisited(){
		return visited;
	}

	public void setVisited(boolean v){
		visited = v;
	}

	public int getDistanceValue(){
		return distanceValue;
	}

	public void setDistanceValue(int d){
		distanceValue = d;
	}

	public GameNode getPreviousNodeInPath(){
		return previousNodeInPath;
	}

	public void setPreviousNodeInPath(GameNode prev){
		previousNodeInPath = prev;
	}

	public boolean isVerifier(){
		return player.equals("V");
	}

	public boolean isRefuter(){
		return player.equals("R") || isErrState();
	}

	public boolean isNumbered(){
		return numbered;
	}

	public void setNumbered(boolean b){
		numbered = b;
	}

	//EXPANSION SET
	public boolean isProbabilistic(){
		return player.equals("P");
	}
	//EXPANSION SET
  	public void setValue(int i, double val){
  		values[i] = val;
  	}
  	//EXPANSION SET
  	public Double[] getValues(){
	    return values;
	}

	@Override
	public int compareTo(Object u) {
		if (u instanceof GameNode)
			if (this.equals((GameNode)u))
				return 0;
		return -1;
	}

	@Override
	public int hashCode(){
	    return Objects.hash(specState, impState, symbol, player);
	}


	public String toString(){
		String res;
		if (this.isErrState())
			res = "ERR_STATE";
		else
			res = "SPEC: "+specState.toString()+", SYMBOL: "+(symbol.isFromSpec()?"S":(symbol.getLabel().equals("")?"#":"I"))+symbol.getLabel()+", IMP:"+impState.toString()+", PLAYER: "+player;
		return res;
	}

	public String toString2(){
		String res;
		if (this.isErrState())
			res = "ERR_STATE";
		else
			res = "SPEC: "+", SYMBOL: "+(symbol.isFromSpec()?"S":(symbol.getLabel().equals("")?"#":"I"))+symbol.getLabel()+", IMP:"+", PLAYER: "+player;
		return res;
	}

	public String toStringDot(){
		String res;
		if (this.isErrState())
			res = "ERR_STATE";
		else{
			String s = symbol.getLabel().equals("")?"":(symbol.getLabel()+(symbol.isFromSpec()?"_S":"_I"));
			res = "Spec: "+specState.toStringDot()+"\nSymbol: "+s+"\nImp: "+impState.toStringDot()+"\nPlayer: "+player+"\nValue: "+distanceValue;
		}
		return res;
	}

	public boolean isErrState(){
		return symbol.getLabel().equals("ERR");
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof GameNode){
			GameNode n = (GameNode)o;
			if (this.isErrState() && n.isErrState())
					return true;
			else{
				if (this.isErrState() || n.isErrState())
					return false;
			}
				
			if (specState == n.getSpecState() && impState == n.getImpState() && symbol.equals(n.getSymbol()) && player.equals(n.getPlayer())){
				//System.out.println(this.hashCode()==n.hashCode());
				return true;
			}
			//System.out.println(this.hashCode()==n.hashCode());
		}
		return false;
	}
	
}