package lang;



public class MultIntExp extends NumExp{
	Expression exp1; // the left int
	Expression exp2; // the right int
   
    
    public MultIntExp(Expression exp1, Expression exp2){
    	
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
