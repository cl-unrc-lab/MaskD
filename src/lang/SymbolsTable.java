package lang;

import java.util.*;

public class SymbolsTable {
	
	private ArrayList<TableLevel> table;
	private int currentLevel;


	public SymbolsTable() {
		table = new ArrayList<TableLevel>();
		currentLevel = 0;
		table.add(currentLevel,new TableLevel());
	}


	public int getLevel() {
		return currentLevel;
	}

	public void incrementLevel() {
		currentLevel++;
		table.add(currentLevel,new TableLevel());
	}

	public void decrementLevel() {
		table.get(currentLevel).deleteLevel();
		currentLevel--;
	}
	
	public boolean addSymbol(Var var) {
		if (!existsVar(var.getName(),currentLevel)) {
			table.get(currentLevel).addVar(var);
			return true;
		} else {
			return false;
		}
	}
    
	public boolean addSymbol(Param par) {
		if (!exists(par.getDeclarationName(),currentLevel)) { //if already not exist an element with the same id at the given level.
			table.get(currentLevel).addParam(par);
			return true;
		} else {
			return false;
		}
	}

	public boolean addSymbol(Proc proc) {
		if (!existsProcess(proc.getName(),currentLevel)) {
			table.get(currentLevel).addProcess(proc);
			return true;
		} else {
			return false;
		}
	}

	public boolean addSymbol(EnumType eType) {
		if (!existsEnumeratedType(eType.getName(),currentLevel)) {
			table.get(currentLevel).addEnumeratedType(eType);
			return true;
		} else {
			return false;
		}
	}
    
	public TableLevel getLevelSymbols(int level) {
		return table.get(level);
	}
	
	public void makeEmpty() {
		table.clear();
	}

	public void emptyCurrentLevel() {
		table.get(currentLevel).deleteLevel();
	}

	private boolean exists(String name,int level) {	
		return this.getLevelSymbols(level).existNameLevel(name);
	}
	
	private boolean existsVar(String name,int level) {
		
		return table.get(level).existVar(name);
	}

	private boolean existsParam(String name,int level) {
		
		return table.get(level).existParam(name);
	}

	private boolean existsProcess(String name,int level) {
		
		return table.get(level).existProcess(name);
	}

	private boolean existsEnumeratedType(String name,int level) {
		
		return table.get(level).existEnumeratedType(name);
	}

	public Var searchVar(String id,int level) {
		
		return this.getLevelSymbols(level).getVar(id);
	}
	
	public Param searchParam(String id,int level) {
		
		return this.getLevelSymbols(level).getParam(id);
	}
	
	public Proc searchProcess(String id,int level) {
		
		return this.getLevelSymbols(level).getProcess(id);
	}
    
	public EnumType searchEnumeratedType(String id,int level) {
		
		return this.getLevelSymbols(level).getEnumeratedType(id);
	}


	
}