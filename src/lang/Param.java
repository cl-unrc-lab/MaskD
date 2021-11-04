package lang;


public class Param extends Expression {

	String declarationName; // Name used in the parameter declaration.
    
	Type type;
    String enumName;
    
	boolean isDeclaration;
    private EnumType enumType;


	public Param(String declName, Type t){
		this.declarationName = declName;
     	this.type = t;
		this.isDeclaration=true;
        this.enumType=null;
        
        Type tVar;
        if(t.isEnumerated()){
            this.enumName = new String(t.getStringValue());
            
            tVar= Type.ENUMERATED;
            tVar.setStringValue(t.getStringValue());
            this.type = tVar;
            
            
        }else{
            this.type = t;
        }
	}
	

	public Param(String declName){
		this.declarationName = declName;
     	this.type = Type.UNDEFINED;
		this.isDeclaration=false;
        this.enumType=null;
        this.enumName=null;
	}

	public Type getType(){
		
		return this.type;
	}
	
	public boolean isDeclaration(){
    	return this.isDeclaration;
    }

    public String getDeclarationName(){
		
		return this.declarationName;
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
		
		this.type = t;
	}
    
	public void setEnumType(EnumType t){	
		this.enumType = t;
	}
	
   
	public EnumType getEnumType( ){		
		return this.enumType;
	}
	
    public void setEnumName(String n){		
		this.enumName = n;
	}
    
    public String getEnumName(){
        return enumName;
    }

    public void setDeclarationName(String n){	
		this.declarationName = n;
	}
    
    
    
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}
	
}

