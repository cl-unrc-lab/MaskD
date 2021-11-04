package lang;
import java.util.LinkedList;


public class ProbAssign extends Code{

    LinkedList<Code> assigns; 
    Double probability;
    
    
    public ProbAssign(Double exp, LinkedList<Code> assigns){
        this.probability = exp;
    	this.assigns = assigns;
    	
    }
    
        
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}
    
    public Double getProbability(){
        return probability;
    }

    public LinkedList<Code> getAssigns(){
        return assigns;
    }
}
