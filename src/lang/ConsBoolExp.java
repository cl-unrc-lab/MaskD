package lang;



public class ConsBoolExp extends BoolExp{

    boolean value;
    
    public ConsBoolExp(boolean value){
    	super();
        this.value = value;
    }
        
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}

    public boolean getValue(){
        return value;
    }
}
