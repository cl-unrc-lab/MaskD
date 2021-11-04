package lang;


public class VarAssign extends Code{

    Var var; // the var in the left part
    Expression exp; 
    
    
    public VarAssign(Var var, Expression exp){
    	// Note: the var and the expression have to be of the same type
    	this.var = var;
    	this.exp = exp;
    	
    }
    
        
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}
    
    public Expression getExp(){
        return exp;
    }

    public Var getVar(){
        return var;
    }
}
