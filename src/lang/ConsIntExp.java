package lang;



public class ConsIntExp extends NumExp{
    
	Integer value;

    public ConsIntExp(Integer i){
      super();
      value = i;
    }
    
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}

    public Integer getValue(){
        return value;
    }
}