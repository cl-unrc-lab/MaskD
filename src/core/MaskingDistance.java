package core;

import java.util.*;
import model.*;
import games.*;
import lang.*;

/**
* Core functionality of MaskD
*  
* @author Luciano Putruele
*/
public class MaskingDistance{

    private GameGraph g; 
    private boolean noBisimulation = false;
    private Model spec;
    private Model imp;
    private String specName, impName;
    private boolean verbose;

    /**
    * Constructor for the class, calls buildGraph and sets up the lineal programming libraries.
    *
    * @param  specProgram  the nominal model
    * @param  impProgram  the fault-tolerant model
    * @param  deadlockIsError treat deadlock states as error (terminal states)?
    * @param  noBisim does the specification not need to simulate the implementation?
    * @param  verb turn verbosity on?
    */
    public MaskingDistance(Program specProgram, Program impProgram, boolean deadlockIsError, boolean noBisim, boolean verb) throws InterruptedException{
        noBisimulation = noBisim;
        System.out.println("Building Models...");
        spec = specProgram.toLTS(true);
        specName = specProgram.getName();
        imp = impProgram.toLTS(false);
        impName = impProgram.getName();
        if (imp.getIsWeak()){
            spec.setIsWeak(true);
            System.out.println("Saturating Models...");
        }
        spec.saturate();
        imp.saturate();
        verbose = verb;
        if (verbose){
            System.out.println("Spec states: "+spec.getNumNodes());
            System.out.println("Spec edges: "+spec.getNumEdges());
            System.out.println("Imp states: "+imp.getNumNodes());
            System.out.println("Imp edges: "+imp.getNumEdges());
        }
        buildGraph(deadlockIsError);
    }

    /**
    * Builds Game Graph
    * @param  deadlockIsError treat deadlock states as error (terminal states)?
    */
    public void buildGraph(boolean deadlockIsError) throws InterruptedException{
        //This method builds a game graph for the Masking Distance Game, there are two players: the Refuter(R) and the Verifier(V)
        //The refuter plays with the implementation(imp), this means choosing any action available (faulty or not)
        //and the verifier plays with the specification(spec), he tries to match the action played by the refuter, if he can't then an error state is reached.        
        System.out.println("Building Game Graph...");
        g = new GameGraph();

        //calculate initial state
        GameNode init = new GameNode(spec.getInitial(), imp.getInitial(),new Action("", false, false, false) ,"R");
        g.addNode(init);
        g.setInitial(init);

        TreeSet<GameNode> iterSet = new TreeSet<GameNode>();
        iterSet.add(g.getInitial());

        //build the game graph
        while(!iterSet.isEmpty()){
            GameNode curr = iterSet.pollFirst();
            
            if (deadlockIsError && imp.getSuccessors(curr.getImpState()).size() == 0 && curr.getPlayer() == "R"){ // special deadlock case
                    Action preErr = new Action("DEADLOCK_ERR", false, false, false);
                    GameNode curr_ = new GameNode(curr.getSpecState(),curr.getImpState(),preErr,"V");
                    g.addNode(curr_);
                    g.addEdge(curr,curr_,preErr);
                    iterSet.add(curr_);
            }
            if (curr.getPlayer() == "R"){ //if player is refuter we add its possible moves from current state
                //IMP MOVES
                for (ModelState succ : imp.getSuccessors(curr.getImpState())){
                    Pair p = new Pair(curr.getImpState(),succ);
                    if (imp.getActions().get(p) != null){
                        for (int i=0; i < imp.getActions().get(p).size(); i++){
                            GameNode curr_ = new GameNode(curr.getSpecState(),succ,imp.getActions().get(p).get(i), "V");
                            GameNode toOld = g.search(curr_);
                            if (toOld == null){
                                g.addNode(curr_);
                                if (curr_.getSymbol().isFaulty())
                                    curr_.setMask(true);
                                g.addEdge(curr,curr_, curr_.getSymbol()); 
                                iterSet.add(curr_);
                            }
                            else{
                                g.addEdge(curr,toOld, toOld.getSymbol());
                            }
                        }
                    }
                }
                //SPEC MOVES
                if (!noBisimulation){
                    for (ModelState succ : spec.getSuccessors(curr.getSpecState())){
                        Pair p = new Pair(curr.getSpecState(),succ);
                        if (spec.getActions().get(p) != null){
                            for (int i=0; i < spec.getActions().get(p).size(); i++){
                                GameNode curr_ = new GameNode(succ,curr.getImpState(),spec.getActions().get(p).get(i), "V");
                                GameNode toOld = g.search(curr_);
                                if (toOld == null){
                                    g.addNode(curr_);
                                    g.addEdge(curr,curr_, curr_.getSymbol()); 
                                    iterSet.add(curr_);
                                }
                                else{
                                    g.addEdge(curr,toOld,toOld.getSymbol()); 
                                }
                            }
                        }
                    }
                }
            }
            else{ //if player is verifier we add its matching move from current state or err transition if can't match
                boolean foundSucc = false;
                //SPEC MOVES
                if (!curr.getSymbol().isFromSpec()){
                    if (curr.getMask()){ // the state has to mask a previous fault
                        GameNode curr_ = new GameNode(curr.getSpecState(),curr.getImpState(),new Action("",false,false,false), "R");
                        GameNode toOld = g.search(curr_);
                        if (toOld == null){
                            g.addNode(curr_);
                            g.addEdge(curr,curr_,new Action("Mask_"+curr.getSymbol().getLabel(),false,false,true,true));
                            iterSet.add(curr_);
                        }
                        else{
                            g.addEdge(curr,toOld,new Action("Mask_"+curr.getSymbol().getLabel(),false,false,true,true));
                        }
                        foundSucc = true;
                    }
                    else{
                        for (ModelState succ : spec.getSuccessors(curr.getSpecState())){
                            Pair p = new Pair(curr.getSpecState(),succ);
                            if (spec.getActions().get(p) != null){
                                for (int i=0; i < spec.getActions().get(p).size(); i++){
                                    Action lblImp = curr.getSymbol();
                                    Action lblSpec = spec.getActions().get(p).get(i);
                                    if (lblImp.getLabel().equals(lblSpec.getLabel()) || (lblImp.isTau() && lblSpec.isTau())){
                                        GameNode curr_ = new GameNode(succ,curr.getImpState(), new Action("",false,false,false), "R");
                                        GameNode toOld = g.search(curr_);
                                        if (toOld == null){
                                            g.addNode(curr_);
                                            g.addEdge(curr,curr_, lblSpec); //add label may not be necessary
                                            iterSet.add(curr_);
                                        }
                                        else{
                                            g.addEdge(curr,toOld, lblSpec);
                                        }
                                        foundSucc = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                else{//IMP MOVES
                    if (!noBisimulation){
                        for (ModelState succ : imp.getSuccessors(curr.getImpState())){
                            Pair p = new Pair(curr.getImpState(),succ);
                            if (imp.getActions().get(p) != null){
                                for (int i=0; i < imp.getActions().get(p).size(); i++){
                                    Action lblSpec = curr.getSymbol();
                                    Action lblImp = imp.getActions().get(p).get(i);
                                    if (lblImp.getLabel().equals(lblSpec.getLabel()) || (lblImp.isTau() && lblSpec.isTau())){
                                        GameNode curr_ = new GameNode(curr.getSpecState(),succ, new Action("",false,false,false), "R");
                                        GameNode toOld = g.search(curr_);
                                        if (toOld == null){
                                            g.addNode(curr_);
                                            g.addEdge(curr,curr_, lblImp); //add label may not be necessary
                                            iterSet.add(curr_);
                                        }
                                        else{
                                            g.addEdge(curr,toOld, lblImp);
                                        }
                                        foundSucc = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (!foundSucc){
                    g.addEdge(curr,g.getErrState(),new Action("ERR", false, false, false));
                }
            }
        }
        if (verbose){
            System.out.println("Game graph states: "+g.getNumNodes());
            System.out.println("Game graph edges: "+g.getNumEdges());
        }
    }

    /**
    * Calculate Masking Distance using fix point algorithm, this is the general solution (default)
    *
    * @return real value between 0 and 1 representing the masking distance value at initial vertex
    */
    public double calculateDistanceFixPoint(){
        System.out.println("Calculating Distance...");

        g.getErrState().setDistanceValue(1);
        GameNode n = g.getErrState();

        LinkedList<GameNode> q = new LinkedList<GameNode>();
        q.addFirst(n);
        boolean change;
        while (!q.isEmpty()) {
            GameNode v = q.getFirst();
            q.removeFirst();
            for (GameNode adj : g.getPredecessors(v)) {
                change = false;
                int addedCost;
                int m = adj.isVerifier()?maxD(adj):minD(adj);
                addedCost =  (m==Integer.MAX_VALUE?0:(adj.getSymbol().isFaulty()?1:0));
                if (adj.getDistanceValue() != m + addedCost){
                    change = true;
                    adj.setDistanceValue(m + addedCost);
                }
                if (change){
                    q.addLast(adj);
                }
            }
        }

        int minDistance = g.getInitial().getDistanceValue();        
        double res = Math.round((double)1/(minDistance) * Math.pow(10, 3)) / Math.pow(10, 3);
        return res;
    }

    private int minD (GameNode n){
        int min = Integer.MAX_VALUE;
        for (GameNode m : g.getSuccessors(n)){
            if (m.getDistanceValue() < min)
                min = m.getDistanceValue();
        }
        return min;
    }

    private int maxD (GameNode n){
        int max = Integer.MIN_VALUE;
        for (GameNode m : g.getSuccessors(n)){
            if (m.getDistanceValue() > max)
                max = m.getDistanceValue();
        }
        return max;
    }

    private GameNode strategyR (GameNode n){
        int min = Integer.MAX_VALUE;
        GameNode strategy = null;
        for (GameNode m : g.getSuccessors(n)){
            if (m.getDistanceValue() < min){
                min = m.getDistanceValue();
                strategy = m;
            }
            if (m.getDistanceValue() == min && m.getDistanceToError() < strategy.getDistanceToError()){
               strategy = m; 
            }
        }
        return strategy;
    }

    private GameNode strategyV (GameNode n){
        int max = Integer.MIN_VALUE;
        GameNode strategy = null;
        for (GameNode m : g.getSuccessors(n)){
            if (m.getDistanceValue() > max){
                max = m.getDistanceValue();
                strategy = m;
            }
        }
        return strategy;
    }
    
    /**
    * Calculate Masking Distance using shortest path algorithm, this solution has as precondition the models being deterministic
    * in their labels, i.e. no two actions available at any state have the same label. 
    *
    * @return real value between 0 and 1 representing the masking distance value at initial vertex
    */
    public double calculateDistanceShortestPath(){
        System.out.println("Calculating Distance...");

        g.getErrState().setDistanceValue(1);
        
        LinkedList<GameNode> q = new LinkedList<GameNode>() ;
        q.addFirst(g.getErrState());
        while (!q.isEmpty()) {
            GameNode v = q.getFirst();
            q.removeFirst();
            for (GameNode adj : g.getPredecessors(v)) {
                int w = adj.getSymbol().isFaulty()?1:0;
                if (v.getDistanceValue() + w < adj.getDistanceValue()) {
                    adj.setDistanceValue(v.getDistanceValue() + w);
                    if (w == 1 && !adj.getVisited()){
                        adj.setVisited(true);
                        q.addLast(adj);
                    }
                    else{
                        if (!adj.getVisited()){
                            adj.setVisited(true);
                            q.addFirst(adj);
                        }
                    }
                }
            }
        }

        int minDistance = g.getInitial().getDistanceValue();
        
        double res= Math.round((double)1/(minDistance) * Math.pow(10, 3)) / Math.pow(10, 3);
        return res;
    }

    /**
    * Print a the optimal path/strategies to the error state
    */
    public void printTraceToError(){
        if (g.getInitial().getDistanceValue() == Integer.MAX_VALUE){
            System.out.println("No trace to error!");
            return;
        }
        computeDistancesToError();
        System.out.println("\n·····ERROR PATH·····\n");
        GameNode curr = g.getInitial();
        int i = 0;
        while (curr != g.getErrState()){
            System.out.println(i+". "+curr.toString());
            if (curr.isVerifier())
                curr = strategyV(curr);
            else
                curr = strategyR(curr);
            i++;
        }
        System.out.println(i+". "+curr.toString());
    }

    private void computeDistancesToError(){
        g.getErrState().setDistanceToError(0);
        
        LinkedList<GameNode> q = new LinkedList<GameNode>() ;
        q.addFirst(g.getErrState());
        while (!q.isEmpty()) {
            GameNode v = q.getFirst();
            q.removeFirst();
            for (GameNode adj : g.getPredecessors(v)) {
                if (!adj.getVisited()){
                    adj.setVisited(true);
                    adj.setDistanceToError(v.getDistanceToError() + 1);
                    q.addLast(adj);
                }
            }
        }
    }

    public void printUnmatches(){
        System.out.println("\n·····ACTIONS LEADING TO ERROR·····\n");
        for (GameNode n : g.getPredecessors(g.getErrState())){
            System.out.println(n);
        }
    }

    public void createDot(int lineLimit){
        spec.createDot(false);
        imp.createDot(true);
        g.createDot(lineLimit, specName+"---"+impName, false);
    }

    /**
    * Initiates an interactive simulation of the game
    */
    public void simulateGame(){
        GameNode curr;
        Stack<GameNode> track = new Stack<GameNode>();
        track.push(g.getInitial());
        try (Scanner sc = new Scanner(System.in)) {
			String c = "";
			System.out.println("\n·····SIMULATION·····\n");
			while (!c.equals("X") && !c.equals("x")){
			    curr = track.peek();
			    System.out.println("\n\nCURRENT STATE: "+curr+"\nChoose an action... (action : {nextstate})\n");
			    Integer i = 0;
			    for (GameNode succ : g.getSuccessors(curr)){
			        Pair p = new Pair(curr,succ);
			        if (g.getActions().get(p) != null){
			            for (int j=0; j < g.getActions().get(p).size(); j++){
			                System.out.println(i+". "+g.getActions().get(p).get(j).getLabel()+": "+""+succ+"");
			                i++;
			            }
			        }
			    }
			    if (track.size()>1){
			        System.out.println("Z. BACKTRACK");
			    }
			    System.out.println("X. EXIT");
			    c = sc.next();

			    i = 0;
			    for (GameNode succ : g.getSuccessors(curr)){
			        Pair p = new Pair(curr,succ);
			        if (g.getActions().get(p) != null){
			            for (int j=0; j < g.getActions().get(p).size(); j++){
			                if (c.equals(i.toString()))
			                    track.push(succ);
			                i++;
			            }
			        }
			    }

			    if (c.equals("Z") || c.equals("z")){
			        if (track.size()>1)
			        track.pop();
			    }
			}
		}
    }
}