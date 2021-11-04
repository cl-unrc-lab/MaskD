package lang;




public class GreaterOrEqualBoolExp extends BoolExp{
	Expression exp1; // left int
	Expression exp2; // right int

    public GreaterOrEqualBoolExp(Expression int1, Expression int2){
    	super();
        this.exp1 = int1;
        this.exp2 = int2;
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