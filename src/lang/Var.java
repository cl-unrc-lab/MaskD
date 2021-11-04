package lang;


public class Var extends Expression {

	String name;
	Type type;
    String enumName;
	boolean isDeclaration;
    private EnumType enumType;


	public Var(String name, Type t){
		this.name = name;
        Type tVar;
        if(t.isEnumerated()){
            this.enumName = new String(t.getStringValue());
            tVar= Type.ENUMERATED;
            tVar.setStringValue(t.getStringValue());
            this.type = tVar;
            
        }
        else{
            this.enumName = null;
            this.type = t;

        }
		this.isDeclaration=true;
        this.enumType=null;
	}
	

	public Var(String name){
		this.name = name;
		this.type = Type.UNDEFINED;
		this.isDeclaration=false;
        this.enumType=null;
        this.enumName = null;


	}


	public Type getType(){
		
		return this.type;
	}
	

	public EnumType getEnumType(){
		
		return this.enumType;
	}
           

    public String getEnumName(){
        return enumName;
    }

	public boolean isDeclaration(){
    	return this.isDeclaration;
    }

    public String getName(){
		
		return name;
	}
    
    
	public void setType(Type t){
		Type tVar;
        if(t.isEnumerated()){            
            tVar= Type.ENUMERATED;
            tVar.setStringValue(t.getStringValue());
            this.type = tVar;
            
        }else{
            this.type = t;
        }
	}
	
    
	public void setEnumType(EnumType t){
		
		this.enumType = t;
	}
    
    public void setName(String n){
		
		this.name = n;
	}
    
    public void setEnumName(String n){
		
		this.enumName = n;
	}

	public boolean isEnumValue(){

		return this.enumName != null;
	}
    
    public boolean hasEnumType(){
    	return type.isEnumerated();
    }
    
    public String toStringComplete(){
        
    	String varInfo = new String("Var:   ");
    	varInfo = varInfo.concat("name: "+this.name + " - ");
    	varInfo = varInfo.concat("type: "+this.type.toString()+ " - ");
    	varInfo = varInfo.concat("isDeclaration: " +this.isDeclaration +"\n");
    	return varInfo;
    }

    
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}
	
}

