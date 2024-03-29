package lang;

import java.util.*;

import model.*;




public class Program extends ProgramNode{
    LinkedList<EnumType> enumTypes;
    GlobalVarCollection globalVars;
    ProcessCollection process;
    Main mainProgram;
    int maxEnumSize;
    String name;
    
    public Program(GlobalVarCollection gVars, ProcessCollection process, Main mainProgram){
        this.enumTypes = new LinkedList<EnumType>();
        this.globalVars = gVars;
        this.process = process;
        this.mainProgram = mainProgram;
        this.maxEnumSize = 0;
    }
    

    public Program( ProcessCollection process, Main mainProgram){
        this.enumTypes = new LinkedList<EnumType>();
        this.globalVars = new GlobalVarCollection();
        this.process = process;
        this.mainProgram = mainProgram;
    }
    
    public Program(LinkedList<EnumType> enumList, GlobalVarCollection gVars, ProcessCollection process, Main mainProgram){
        this.enumTypes = enumList;
        this.globalVars = gVars;
        this.process = process;
        this.mainProgram = mainProgram;
        
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
    }
    
    public Program(LinkedList<EnumType> enumList,ProcessCollection process, Main mainProgram){
        this.enumTypes = enumList;
        this.globalVars = new GlobalVarCollection();
        this.process = process;
        this.mainProgram = mainProgram;
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
    }
    
    public GlobalVarCollection getGlobalVars(){
        return this.globalVars;
    }
    
    public LinkedList<EnumType> getEnumList(){
        return this.enumTypes;
    }
    
    public int getMaxEnumSize(){
        return this.maxEnumSize;
    }
    
    public void accept(LangVisitor v){
         v.visit(this);         
    }

    
    public Model toLTS(boolean isSpec){
        Model m = new Model(globalVars, isSpec);

        //states in m are lists of states (from processes)
        //calculate initial state
        ModelState init = new ModelState(m);
        for (int j=0; j < mainProgram.getProcessDecl().size(); j++){
            ProcessDecl pDecl = mainProgram.getProcessDecl().get(j);
            for (int i = 0; i < process.getProcessList().size(); i++){
                Proc proc = process.getProcessList().get(i);
                if (pDecl.getType().equals(proc.getName())){
                    init.getModel().getProcs().add(proc);
                    init.getModel().getProcDecls().add(pDecl.getName());
                    init.evalInit(proc.getInitialCond(),j);
                }
            }
        }
        
        m.addNode(init);
        m.setInitial(init);

        TreeSet<ModelState> iterSet = new TreeSet<ModelState>();
        iterSet.add(m.getInitial());
        //build the whole model
        while(!iterSet.isEmpty()){
            ModelState curr = iterSet.pollFirst();
            for (int i = 0; i < m.getProcDecls().size(); i++){ // for each process in current global state
                for (Branch b : m.getProcs().get(i).getBranches()){
                    if (b.getIsTau())
                        m.setIsWeak(true);
                    if (curr.satisfies(b.getGuard(),i)){
                        //create global successor curr_
                        ModelState curr_ = curr.createSuccessor(b.getAssignList(),i);
                        ModelState toOld = m.search(curr_);
                        Action act = new Action(m.getProcDecls().get(i)+"."+b.getLabel(),b.getIsFaulty(),b.getIsTau(),b.getReward(),isSpec);
                        if (toOld == null){
                            m.addNode(curr_);
                            iterSet.add(curr_);
                            m.addEdge(curr, curr_, act);
                        }
                        else{
                            m.addEdge(curr, toOld, act);
                        }
                    }
                }
            }
        }
        return m;
    }
    

    public void setName(String n){
        name = n;
    }

    public String getName(){
        return name;
    }

}
