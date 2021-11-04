package lang;

import java.util.LinkedList;

public class GlobalVarCollection extends ProgramNode{
	
	LinkedList<Var> boolVars;
	LinkedList<Var> intVars;
    LinkedList<Var> enumVars;
    


	public GlobalVarCollection(){
		boolVars = new LinkedList<Var>();
		intVars = new LinkedList<Var>();
        enumVars = new LinkedList<Var>();
	}
	
	public int getTotalGlobalBoolVars(){
		return boolVars.size();
	}
	
	
	public int getTotalGlobalIntVars(){
		return intVars.size();
	}

    public int getTotalGlobalEnumVars(){
		return enumVars.size();
	}
	
    
	public boolean addGlobalBoolVar(Var v){
		return boolVars.add(v);
	}
	
	public boolean addGlobalIntVar(Var v){
		return intVars.add(v);
	}
	
	public boolean addGlobalEnumVar(Var var){
        return enumVars.add(var);
	}
	
    
    public LinkedList<Var> getBoolVars(){
		return this.boolVars;
	}
	
	public LinkedList<Var> getIntVars(){
		return this.intVars;
	}

    public LinkedList<Var> getEnumVars(){
		return this.enumVars;
	}


	public void accept(LangVisitor v){
	     v.visit(this);			
	}

}
