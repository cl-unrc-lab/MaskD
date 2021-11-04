package lang;


import java.util.*;

public class ProcessCollection extends ProgramNode {
	
	
	LinkedList<Proc> processList;
	int totalIntVars;
	int totalBoolVars;
    int totalEnumVars;
	
	public ProcessCollection(){
		
		processList = new LinkedList<Proc>();
		totalIntVars=0;
		totalBoolVars = 0;
        totalEnumVars=0;
	
	}
	
    public void addProcess(Proc proc){
    	processList.add(proc);
    	totalIntVars += proc.getNumVarInt();
    	totalBoolVars += proc.getNumVarBool();
        totalEnumVars += proc.getNumVarEnumerated();
    }
    
    public int getTotalIntVars(){
    	return totalIntVars;
    }
    
    public int getTotalBoolVars(){
    	return totalBoolVars;
    }
    
    public int getTotalEnumVars(){
    	return totalEnumVars;
    }
    
    
    public LinkedList<Proc> getProcessList(){
    	return processList;
    }
    
    public boolean existProcess(String name){
		boolean found =false;
		int i = 0;
		
		while ( i < processList.size() && !found ){
			if (processList.get(i).equals(name)){
				found =true;
			}
			i++;
	    }
		
		return found;
		
	} 
    
    
    public Proc getProcess(String name){
		boolean found =false;
		Proc proc = null;
		
		int i = 0;
		
		
		while ( i < processList.size() && !found ){
			
			if (processList.get(i).getName().equals(name)){
				proc = processList.get(i);
				found =true;
			}
			i++;
	    }
		
		return proc;
		
	} 
    
    
    
	public void accept(LangVisitor v){
	     v.visit(this);			
	}

}
