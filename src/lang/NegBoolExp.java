package lang;




public class NegBoolExp extends BoolExp{
   
	Expression exp; // the expresison to be negated
    

    public NegBoolExp(Expression exp){
    
        this.exp = exp;
    }
    
       
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}

    public Expression getExp(){
        return exp;
    }
}
