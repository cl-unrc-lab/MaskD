package lang;



public class ConsDoubleExp extends NumExp{
    
	Double value;

    public ConsDoubleExp(Double i){
      super();
      value = i;
    }
    
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}

    public Double getValue(){
        return value;
    }
}