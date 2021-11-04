package lang;


public class EnumType extends ProgramNode{
	String name; // the name of the enum type
	String[] cons; // the constants belonging to the type
	int size; // the size of the enumerable type
    int numVars; // the number of variables of this type in the program

	
	public EnumType(String name, int size){
		this.name = name;
		this.size = size;
		cons = new String[size];
        numVars = 0;
	}
	

	public String getName(){
		return name;
	}
	

	public int getSize(){
		return size;
	}
	

	public int getNumVars(){
		return numVars;
	}
	
    
	public boolean existConstant(String name){
		for (int i=0; i < size; i++){
			if (cons[i].equals(name))
				return true;
		}
		// no id for the name
		return false;
	}


	public int getConsId(String name){
		for (int i=0; i < size; i++){
			if (cons[i].equals(name))
				return i;
		}
		// no id for the name
		return -1;
	}
    
    public void incrementNumVar(){
        numVars = numVars + 1;
    }
    
    public void setNumVars(int num){
        numVars = num;
    }
    
	public String getCons(int pos){
		for (int i=0; i < size; i++){
			if (i==pos)
				return cons[i];
		}
		// no pos for the name
		return null;
	}
	
	public void addCons(String name, int pos){
		cons[pos] = name;
	}
    
    @Override
	public void accept(LangVisitor v){
        v.visit(this);
	}

	
	
}
