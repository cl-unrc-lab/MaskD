package md;


import lang.*;
import core.*;


public class MainMD {
     
    
    public static void main(String[] args)
    {
       ProgramParser prog = new ProgramParser();
       boolean printTrace = false;
       boolean toDot = false;
       boolean startSimulation = false;
       boolean deadlockIsError = false;
       boolean noBisim = false;
       boolean deterministic = false;
       boolean verbose = false;
       
       if (args.length < 2){
          printHelp();
       }
       else{
           for (int i = 0; i < args.length; i++){
              if (args[i].equals("-t")){
                printTrace = true;
              }
              if (args[i].equals("-s")){
                startSimulation = true;
              }
              if (args[i].equals("-v")){
                verbose = true;
              }
              if (args[i].equals("-d")){
                toDot = true;
              }
              if (args[i].equals("-l")){
                deadlockIsError = true;
              }
              if (args[i].equals("-nb")){
                noBisim = true;
              }
              if (args[i].equals("-det")){
                deterministic = true;
              }
           }
            Program spec = prog.parseAux(args[args.length - 2]);
            Program imp = prog.parseAux(args[args.length - 1]);
            try{
              MaskingDistance md = new MaskingDistance(spec,imp,deadlockIsError,noBisim,verbose);
              if (printTrace){
                  md.printTraceToError();
              }
              else{
                if (startSimulation){
                    md.simulateGame();
                }
                else{
                  if (!deterministic)
                    System.out.println("Masking Distance: "+md.calculateDistance());
                  else
                    System.out.println("Masking Distance: "+md.calculateDistanceBFS());
                }
              }
              if (toDot)
                md.createDot(5000);
            }
            catch(Exception e){
              System.out.println("An error occurred");
            }
        }
     }

     private static void printHelp(){
      System.out.println("MaskD: Masking Distance Tool\n");
      System.out.println("Usage: ./maskD <options> <specification path> <implementation path>\n");
      System.out.println("Options:");
      System.out.println("            -nb : toggle if faulty model do not need to simulate nominal model");
      System.out.println("            -det : use deterministic masking distance algorithm");
      System.out.println("            -d : create dot file");
      System.out.println("            -t : print error trace (only works with -det)");
      System.out.println("            -s : start simulation");
      System.out.println("            -l : also treat deadlock as error state");
      System.out.println("            -v : turn verbosity on");
     }
}