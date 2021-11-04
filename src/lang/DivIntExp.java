package lang;




public class DivIntExp extends NumExp{
	Expression exp1; // left integer
	Expression exp2; // right integer
     
    public DivIntExp(Expression int1, Expression int2){
    	
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
