package lang;




public class OrBoolExp extends BoolExp{
   
    Expression exp1;
    Expression exp2;
    
    public OrBoolExp(Expression exp1, Expression exp2){
    	super();
        this.exp1 = exp1;
        this.exp2 = exp2;
    } 
    
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}

    public Expression getExp1(){
        return exp1;
    }

    public Expression getExp2(){
        return exp2;
    }
}
